require('dotenv').config();
const app = require('./src/app');
const { connectDB } = require('./src/config/db');

const PORT = process.env.PORT || 5000;

connectDB().then(() => {
  app.listen(PORT, () => {
    console.log(`=======================================================`);
    console.log(`🚀 MUDRA CHECK BACKEND SERVER ONLINE`);
    console.log(`📡 Local Port: http://localhost:${PORT}`);
    console.log(`🩺 Health API: http://localhost:${PORT}/api/health`);
    console.log(`📊 Admin Web: http://localhost:${PORT}/admin/index.html`);
    console.log(`=======================================================`);
  });
});
