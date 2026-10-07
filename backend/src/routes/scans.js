const express = require('express');
const router = express.Router();
const scanController = require('../controllers/scanController');
const { authenticate } = require('../middleware/auth');
const { authorizeRoles } = require('../middleware/roles');
const { logAudit } = require('../middleware/auditLogger');

router.post('/', authenticate, logAudit('CREATE_SCAN', 'SCAN'), scanController.createScan);
router.post('/sync', authenticate, logAudit('OFFLINE_SYNC_BATCH', 'SCAN'), scanController.syncOfflineScans);
router.get('/', authenticate, scanController.getScans);
router.get('/:id', authenticate, scanController.getScanById);
router.delete('/:id', authenticate, authorizeRoles('ADMIN'), logAudit('DELETE_SCAN', 'SCAN'), scanController.deleteScan);

module.exports = router;
