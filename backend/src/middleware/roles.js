const authorizeRoles = (...allowedRoles) => {
  return (req, res, next) => {
    if (!req.user || !allowedRoles.includes(req.user.role)) {
      return res.status(403).json({
        success: false,
        error: {
          code: 'FORBIDDEN_ROLE',
          message: `Access denied. Role '${req.user ? req.user.role : 'ANONYMOUS'}' does not have sufficient clearance.`
        }
      });
    }
    next();
  };
};

module.exports = { authorizeRoles };
