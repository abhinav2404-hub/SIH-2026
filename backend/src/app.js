const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const path = require('path');

const { generalLimiter } = require('./middleware/rateLimit');
const { errorHandler } = require('./middleware/errorHandler');

const authRoutes = require('./routes/auth');
const scanRoutes = require('./routes/scans');
const rulesetRoutes = require('./routes/rulesets');
const complaintRoutes = require('./routes/complaints');
const deviceRoutes = require('./routes/devices');
const dashboardRoutes = require('./routes/dashboard');
const auditRoutes = require('./routes/auditLogs');
const healthRoutes = require('./routes/health');

const app = express();

// Security & Middlewares
app.use(helmet({ contentSecurityPolicy: false }));
app.use(cors({ origin: '*' }));
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));
app.use(morgan('dev'));
app.use(generalLimiter);

// Serve Static Admin Dashboard
app.use(express.static(path.join(__dirname, '../public')));

// Root & Health
app.use('/api/health', healthRoutes);
app.use('/health', healthRoutes);

// API v1 Routing
app.use('/api/v1/auth', authRoutes);
app.use('/api/v1/scans', scanRoutes);
app.use('/api/v1/rulesets', rulesetRoutes);
app.use('/api/v1/complaints', complaintRoutes);
app.use('/api/v1/devices', deviceRoutes);
app.use('/api/v1/dashboard', dashboardRoutes);
app.use('/api/v1/audit-logs', auditRoutes);

// Backward Compatibility Routes for Existing Mobile Scopes
app.use('/api/auth', authRoutes);
app.use('/api/scans', scanRoutes);
app.use('/api/rulesets', rulesetRoutes);
app.use('/api/complaints', complaintRoutes);
app.use('/api/dashboard', dashboardRoutes);

// 404 Route Catch-All
app.use((req, res, next) => {
  res.status(404).json({
    success: false,
    error: {
      code: 'ROUTE_NOT_FOUND',
      message: `The requested endpoint '${req.method} ${req.originalUrl}' does not exist on this server.`
    }
  });
});

// Centralized Error Handling
app.use(errorHandler);

module.exports = app;
