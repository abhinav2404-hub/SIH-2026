const express = require('express');
const router = express.Router();
const dashboardController = require('../controllers/dashboardController');
const { authenticate } = require('../middleware/auth');

router.get('/summary', authenticate, dashboardController.getSummary);
router.get('/violations', authenticate, dashboardController.getViolationsBreakdown);
router.get('/recent-scans', authenticate, dashboardController.getRecentScans);

module.exports = router;
