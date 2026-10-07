const express = require('express');
const router = express.Router();
const rulesetController = require('../controllers/rulesetController');
const { authenticate } = require('../middleware/auth');
const { authorizeRoles } = require('../middleware/roles');
const { logAudit } = require('../middleware/auditLogger');

router.get('/', rulesetController.getRulesets);
router.get('/:id', rulesetController.getRulesetById);
router.post('/', authenticate, authorizeRoles('ADMIN'), logAudit('CREATE_RULESET', 'RULESET'), rulesetController.createRuleset);
router.put('/:id', authenticate, authorizeRoles('ADMIN'), logAudit('UPDATE_RULESET', 'RULESET'), rulesetController.updateRuleset);

module.exports = router;
