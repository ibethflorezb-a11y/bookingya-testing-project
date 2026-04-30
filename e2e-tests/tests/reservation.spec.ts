import { test, expect } from '@playwright/test';

test.describe('Reservation Management API', () => {
  const baseUrl = 'http://127.0.0.1:8081';
  let reservationId: string;
  let roomId: string;
  let guestId: string;

  test.beforeAll(async ({ request }) => {
    console.log(`Verificando conexión con el servidor en ${baseUrl}...`);
    let connected = false;
    for (let i = 0; i < 10; i++) {
      try {
        const response = await request.get(`${baseUrl}/api/rooms`);
        if (response.status() < 500) {
          connected = true;
          console.log('Servidor detectado y respondiendo.');
          break;
        }
      } catch (e) {
        console.log(`Intento ${i + 1}: Servidor no disponible aún, reintentando en 2s...`);
        await new Promise(resolve => setTimeout(resolve, 2000));
      }
    }
    if (!connected) {
      throw new Error('No se pudo conectar con el servidor Spring Boot en el puerto 8081. Asegúrate de que esté corriendo.');
    }
  });

  test('should allow a user to create, view, update, and cancel a reservation via API', async ({ request }) => {
    const timestamp = Date.now();
    
    // 1. Create a Room
    const roomResponse = await request.post(`${baseUrl}/api/rooms`, {
      data: {
        code: `ROOM-${timestamp}`,
        name: 'Deluxe Suite',
        city: 'Medellín',
        maxGuests: 4,
        nightlyPrice: 150.0,
        available: true
      }
    });
    expect(roomResponse.status()).toBe(200);
    const roomData = await roomResponse.json();
    roomId = roomData.id;

    // 2. Create a Guest
    const guestResponse = await request.post(`${baseUrl}/api/guests`, {
      data: {
        identification: `ID-${timestamp}`,
        fullName: 'Juan Perez',
        email: `juan.perez.${timestamp}@example.com`,
        phone: '555-1234'
      }
    });
    expect(guestResponse.status()).toBe(200);
    const guestData = await guestResponse.json();
    guestId = guestData.id;

    // 3. Create a Reservation
    const createResponse = await request.post(`${baseUrl}/api/reservations`, {
      data: {
        roomId: roomId,
        guestId: guestId,
        startDate: '2026-05-01T14:00:00',
        endDate: '2026-05-05T11:00:00',
        guestsCount: 2
      }
    });
    expect(createResponse.status()).toBe(200);
    const createData = await createResponse.json();
    reservationId = createData.id;

    // 4. View Reservation
    const getResponse = await request.get(`${baseUrl}/api/reservations/${reservationId}`);
    expect(getResponse.ok()).toBeTruthy();
    const getData = await getResponse.json();
    expect(getData.id).toBe(reservationId);

    // 5. Update Reservation
    const updateResponse = await request.put(`${baseUrl}/api/reservations/${reservationId}`, {
      data: {
        roomId: roomId,
        guestId: guestId,
        startDate: '2026-05-01T14:00:00',
        endDate: '2026-05-06T11:00:00',
        guestsCount: 3
      }
    });
    expect(updateResponse.ok()).toBeTruthy();
    const updateData = await updateResponse.json();
    expect(updateData.guestsCount).toBe(3);

    // 6. Delete Reservation
    const deleteResponse = await request.delete(`${baseUrl}/api/reservations/${reservationId}`);
    expect(deleteResponse.status()).toBe(200);
  });

  test('should reject reservation with invalid date range', async ({ request }) => {
    const timestamp = Date.now();
    
    // Create Room and Guest
    const roomRes = await request.post(`${baseUrl}/api/rooms`, {
      data: { code: `R-INV-${timestamp}`, name: 'Room', city: 'C', maxGuests: 2, nightlyPrice: 100, available: true }
    });
    const guestRes = await request.post(`${baseUrl}/api/guests`, {
      data: { identification: `ID-INV-${timestamp}`, fullName: 'G', email: `g.${timestamp}@e.com` }
    });
    
    const rData = await roomRes.json();
    const gData = await guestRes.json();

    // Attempt invalid reservation (checkIn after checkOut)
    const res = await request.post(`${baseUrl}/api/reservations`, {
      data: {
        roomId: rData.id,
        guestId: gData.id,
        startDate: '2026-06-10T14:00:00',
        endDate: '2026-06-05T11:00:00',
        guestsCount: 1
      }
    });
    
    expect(res.status()).toBe(400);
    const body = await res.json();
    expect(body.error).toBe('checkIn must be before checkOut');
  });

  test('should reject reservation when capacity is exceeded', async ({ request }) => {
    const timestamp = Date.now();
    
    // Create Room with capacity 2
    const roomRes = await request.post(`${baseUrl}/api/rooms`, {
      data: { code: `R-CAP-${timestamp}`, name: 'Small Room', city: 'C', maxGuests: 2, nightlyPrice: 100, available: true }
    });
    const guestRes = await request.post(`${baseUrl}/api/guests`, {
      data: { identification: `ID-CAP-${timestamp}`, fullName: 'G', email: `cap.${timestamp}@e.com` }
    });
    
    const rData = await roomRes.json();
    const gData = await guestRes.json();

    // Attempt reservation for 5 guests
    const res = await request.post(`${baseUrl}/api/reservations`, {
      data: {
        roomId: rData.id,
        guestId: gData.id,
        startDate: '2026-07-01T14:00:00',
        endDate: '2026-07-05T11:00:00',
        guestsCount: 5
      }
    });
    
    expect(res.status()).toBe(400);
    const body = await res.json();
    expect(body.error).toBe('guestsCount exceeds room capacity');
  });
});
