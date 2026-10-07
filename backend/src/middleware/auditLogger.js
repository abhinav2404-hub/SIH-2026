const AuditLog = require('../models/AuditLog');

const logAudit = (action, entityType) => {
  return async (req, res, next) => {
    // Record audit asynchronously without blocking primary response
    res.on('finish', async () => {
      if (res.statusCode >= 200 && res.statusCode < 400) {
        try {
          const entityId = req.params.id || req.body.clientScanId || req.body.rulesetId || req.body.complaintId || '';
          await AuditLog.create({
            userId: req.user ? req.user._id : null,
            deviceId: req.body.deviceId || req.headers['x-device-id'] || '',
            action,
            entityType,
            entityId,
            details: {
              method: req.method,
              path: req.originalUrl,
              statusCode: res.statusCode
            },
            ipAddress: req.ip || req.connection.remoteAddress || '127.0.0.1',
            userAgent: req.headers['user-agent'] || ''
          });
        } catch (err) {
          console.error('[AuditLog] Error recording audit event:', err.message);
        }
      }
    });
    next();
  };
};

module.exports = { logAudit };
