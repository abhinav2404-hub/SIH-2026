const express = require('express');
const router = express.Router();
const AuditLog = require('../models/AuditLog');
const { authenticate } = require('../middleware/auth');
const { authorizeRoles } = require('../middleware/roles');

router.get('/', authenticate, authorizeRoles('ADMIN'), async (req, res, next) => {
  try {
    const logs = await AuditLog.find()
      .sort({ createdAt: -1 })
      .limit(50)
      .populate('userId', 'name email role');

    res.json({
      success: true,
      data: logs
    });
  } catch (error) {
    next(error);
  }
});

module.exports = router;
