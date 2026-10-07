const request = require('supertest');
const app = require('../src/app');

describe('Mudra Check REST API Health & Routing Verification', () => {
  it('GET /api/health returns valid server and database status', async () => {
    const res = await request(app).get('/api/health');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('status');
    expect(res.body).toHaveProperty('version', '2.0.0');
  });

  it('POST /api/v1/auth/register fails gracefully with missing parameters', async () => {
    const res = await request(app)
      .post('/api/v1/auth/register')
      .send({ email: 'test@example.com' });
    expect(res.statusCode).toBe(400);
    expect(res.body.success).toBe(false);
  });

  it('GET /api/v1/rulesets returns active statutory regulatory frameworks', async () => {
    const res = await request(app).get('/api/v1/rulesets');
    expect(res.statusCode).toBe(200);
    expect(res.body.success).toBe(true);
    expect(Array.isArray(res.body.data)).toBe(true);
  });

  it('Rejects unauthorized access to sensitive inspection records', async () => {
    const res = await request(app).get('/api/v1/scans');
    expect(res.statusCode).toBe(401);
  });
});
