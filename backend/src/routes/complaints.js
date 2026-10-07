const express = require('express');
const router = express.Router();
const complaintController = require('../controllers/complaintController');
const { authenticate } = require('../middleware/auth');
const { authorizeRoles } = require('../middleware/roles');
const { logAudit } = require('../middleware/auditLogger');

router.post('/', authenticate, logAudit('SUBMIT_COMPLAINT', 'COMPLAINT'), complaintController.createComplaint);
router.get('/', authenticate, complaintController.getComplaints);
router.patch('/:id', authenticate, authorizeRoles('ADMIN', 'INSPECTOR'), logAudit('UPDATE_COMPLAINT_STATUS', 'COMPLAINT'), complaintController.updateComplaintStatus);

module.exports = router;
