const Device = require('../models/Device');

exports.registerOrHeartbeatDevice = async (req, res, next) => {
  try {
    const { deviceId, deviceName, model, platform, appVersion, osVersion, role, locale, country } = req.body;
    if (!deviceId) {
      return res.status(400).json({
        success: false,
        error: { code: 'MISSING_DEVICE_ID', message: 'deviceId is required.' }
      });
    }

    const device = await Device.findOneAndUpdate(
      { deviceId },
      {
        deviceId,
        userId: req.user ? req.user._id : null,
        ...(deviceName && { deviceName }),
        ...(model && { model }),
        ...(platform && { platform }),
        ...(appVersion && { appVersion }),
        ...(osVersion && { osVersion }),
        ...(role && { role }),
        ...(locale && { locale }),
        ...(country && { country }),
        lastSeenAt: new Date()
      },
      { upsert: true, new: true, setDefaultsOnInsert: true }
    );

    res.json({
      success: true,
      data: device,
      message: 'Device heartbeat registered.'
    });
  } catch (error) {
    next(error);
  }
};

exports.getDevices = async (req, res, next) => {
  try {
    const devices = await Device.find().sort({ lastSeenAt: -1 });
    res.json({
      success: true,
      data: devices
    });
  } catch (error) {
    next(error);
  }
};
