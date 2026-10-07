const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');
const { authenticate } = require('../middleware/auth');
const { authLimiter } = require('../middleware/rateLimit');
const { logAudit } = require('../middleware/auditLogger');

router.post('/register', authLimiter, authController.register);
router.post('/login', authLimiter, logAudit('USER_LOGIN', 'AUTH'), authController.login);
router.post('/refresh', authController.refresh);
router.get('/me', authenticate, authController.me);

module.exports = router;
