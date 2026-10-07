const User = require('../models/User');
const jwt = require('jsonwebtoken');

const signToken = (user) => {
  return jwt.sign(
    { id: user._id, role: user.role, badge: user.badgeNumber },
    process.env.JWT_SECRET || 'mudra_check_secure_jwt_secret_token_2026_xyz',
    { expiresIn: process.env.JWT_EXPIRES_IN || '7d' }
  );
};

const signRefreshToken = (user) => {
  return jwt.sign(
    { id: user._id },
    process.env.JWT_REFRESH_SECRET || 'mudra_check_secure_refresh_secret_2026_abc',
    { expiresIn: process.env.JWT_REFRESH_EXPIRES_IN || '30d' }
  );
};

exports.register = async (req, res, next) => {
  try {
    const { name, email, password, phone, role, badgeNumber } = req.body;
    if (!name || !email || !password) {
      return res.status(400).json({
        success: false,
        error: { code: 'MISSING_FIELDS', message: 'Name, email, and password are required.' }
      });
    }

    const existing = await User.findOne({ email });
    if (existing) {
      return res.status(409).json({
        success: false,
        error: { code: 'USER_EXISTS', message: 'A user with this email address already exists.' }
      });
    }

    const user = await User.create({
      name,
      email,
      password,
      phone,
      role: role || 'INSPECTOR',
      badgeNumber: badgeNumber || `LMO-${Date.now().toString().slice(-4)}`
    });

    const token = signToken(user);
    const refreshToken = signRefreshToken(user);

    res.status(201).json({
      success: true,
      data: {
        token,
        refreshToken,
        user: { id: user._id, name: user.name, email: user.email, role: user.role, badgeNumber: user.badgeNumber }
      },
      message: 'User account registered successfully.'
    });
  } catch (error) {
    next(error);
  }
};

exports.login = async (req, res, next) => {
  try {
    const { email, password } = req.body;
    if (!email || !password) {
      return res.status(400).json({
        success: false,
        error: { code: 'MISSING_CREDENTIALS', message: 'Email and password are required.' }
      });
    }

    const user = await User.findOne({ email }).select('+password');
    if (!user || !(await user.comparePassword(password))) {
      return res.status(401).json({
        success: false,
        error: { code: 'INVALID_CREDENTIALS', message: 'Invalid email or password.' }
      });
    }

    user.lastLogin = new Date();
    await user.save();

    const token = signToken(user);
    const refreshToken = signRefreshToken(user);

    res.json({
      success: true,
      data: {
        token,
        refreshToken,
        user: { id: user._id, name: user.name, email: user.email, role: user.role, badgeNumber: user.badgeNumber }
      }
    });
  } catch (error) {
    next(error);
  }
};

exports.me = async (req, res, next) => {
  try {
    res.json({
      success: true,
      data: req.user
    });
  } catch (error) {
    next(error);
  }
};

exports.refresh = async (req, res, next) => {
  try {
    const { refreshToken } = req.body;
    if (!refreshToken) {
      return res.status(400).json({
        success: false,
        error: { code: 'MISSING_REFRESH_TOKEN', message: 'Refresh token is required.' }
      });
    }

    const decoded = jwt.verify(refreshToken, process.env.JWT_REFRESH_SECRET || 'mudra_check_secure_refresh_secret_2026_abc');
    const user = await User.findById(decoded.id);
    if (!user) {
      return res.status(401).json({
        success: false,
        error: { code: 'INVALID_USER', message: 'User not found.' }
      });
    }

    const newToken = signToken(user);
    res.json({
      success: true,
      data: { token: newToken }
    });
  } catch (error) {
    return res.status(401).json({
      success: false,
      error: { code: 'INVALID_REFRESH_TOKEN', message: 'Invalid or expired refresh token.' }
    });
  }
};
