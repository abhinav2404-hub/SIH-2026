const errorHandler = (err, req, res, next) => {
  console.error(`[Error] [${req.method} ${req.url}]:`, err);

  // Mongoose Validation Error
  if (err.name === 'ValidationError') {
    const details = Object.values(err.errors).map(e => ({
      field: e.path,
      message: e.message
    }));
    return res.status(400).json({
      success: false,
      error: {
        code: 'VALIDATION_ERROR',
        message: 'Invalid payload submitted.',
        details
      }
    });
  }

  // Mongoose Duplicate Key (Unique constraint violation)
  if (err.code === 11000) {
    const field = Object.keys(err.keyValue)[0];
    return res.status(409).json({
      success: false,
      error: {
        code: 'DUPLICATE_KEY',
        message: `Resource with field '${field}' value '${err.keyValue[field]}' already exists.`
      }
    });
  }

  // CastError (e.g. invalid ObjectId)
  if (err.name === 'CastError') {
    return res.status(400).json({
      success: false,
      error: {
        code: 'INVALID_ID',
        message: `Malformed identifier parameter '${err.value}'.`
      }
    });
  }

  // Default Internal Error
  const statusCode = err.statusCode || 500;
  res.status(statusCode).json({
    success: false,
    error: {
      code: err.errorCode || 'INTERNAL_SERVER_ERROR',
      message: process.env.NODE_ENV === 'production' ? 'An unexpected server error occurred.' : err.message
    }
  });
};

module.exports = { errorHandler };
