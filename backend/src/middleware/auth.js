const jwt = require('jsonwebtoken');
const User = require('../models/User');

const authenticate = async (req, res, next) => {
  try {
    const authHeader = req.headers.authorization;
    const apiKey = req.headers['x-api-key'] || req.query.apiKey;

    // 1. Fallback to valid Inspector API Key for demo/mobile backward compatibility
    if (apiKey && (apiKey === process.env.API_KEY || apiKey === 'MUDRA_INSPECTOR_LIVE_KEY_8842')) {
      req.user = {
        role: 'INSPECTOR',
        name: 'Field Inspector (API Auth)',
        badgeNumber: 'LMO-DL-2026-0842'
      };
      return next();
    }

    // 2. JWT Authentication
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(401).json({
        success: false,
        error: {
          code: 'UNAUTHORIZED',
          message: 'Authorization token or valid API key required.'
        }
      });
    }

    const token = authHeader.split(' ')[1];
    const decoded = jwt.verify(token, process.env.JWT_SECRET || 'mudra_check_secure_jwt_secret_token_2026_xyz');

    const user = await User.findById(decoded.id).select('-password');
    if (!user || user.status !== 'ACTIVE') {
      return res.status(401).json({
        success: false,
        error: {
          code: 'INVALID_USER',
          message: 'User account not found or deactivated.'
        }
      });
    }

    req.user = user;
    next();
  } catch (error) {
    return res.status(401).json({
      success: false,
      error: {
        code: 'TOKEN_EXPIRED_OR_INVALID',
        message: 'Invalid or expired authorization token.'
      }
    });
  }
};

module.exports = { authenticate };
