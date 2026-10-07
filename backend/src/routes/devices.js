const express = require('express');
const router = express.Router();
const deviceController = require('../controllers/deviceController');
const { authenticate } = require('../middleware/auth');
const { authorizeRoles } = require('../middleware/roles');

router.post('/', deviceController.registerOrHeartbeatDevice);
router.get('/', authenticate, authorizeRoles('ADMIN'), deviceController.getDevices);

module.exports = router;
