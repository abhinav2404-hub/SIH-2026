const Ruleset = require('../models/Ruleset');

exports.getRulesets = async (req, res, next) => {
  try {
    const country = req.query.country;
    const filter = {};
    if (country) filter.countryCode = country.toUpperCase();

    const rulesets = await Ruleset.find(filter).sort({ createdAt: -1 });
    res.json({
      success: true,
      data: rulesets
    });
  } catch (error) {
    next(error);
  }
};

exports.getRulesetById = async (req, res, next) => {
  try {
    const ruleset = await Ruleset.findOne({
      $or: [{ _id: req.params.id }, { rulesetId: req.params.id }]
    });

    if (!ruleset) {
      return res.status(404).json({
        success: false,
        error: { code: 'RULESET_NOT_FOUND', message: 'Ruleset not found.' }
      });
    }

    res.json({
      success: true,
      data: ruleset
    });
  } catch (error) {
    next(error);
  }
};

exports.createRuleset = async (req, res, next) => {
  try {
    const ruleset = await Ruleset.create(req.body);
    res.status(201).json({
      success: true,
      data: ruleset,
      message: 'New jurisdiction ruleset registered successfully.'
    });
  } catch (error) {
    next(error);
  }
};

exports.updateRuleset = async (req, res, next) => {
  try {
    const ruleset = await Ruleset.findOneAndUpdate(
      { $or: [{ _id: req.params.id }, { rulesetId: req.params.id }] },
      req.body,
      { new: true, runValidators: true }
    );

    if (!ruleset) {
      return res.status(404).json({
        success: false,
        error: { code: 'RULESET_NOT_FOUND', message: 'Ruleset not found.' }
      });
    }

    res.json({
      success: true,
      data: ruleset,
      message: 'Ruleset updated successfully.'
    });
  } catch (error) {
    next(error);
  }
};
