/**
 * Storage Abstraction Layer
 * Supports Local, Cloud Storage, or S3/Firebase references
 */
class StorageService {
  static async uploadImage({ buffer, filename, mimeType }) {
    // Return structured URL reference (never store huge raw binary directly in Mongo)
    const storageProvider = process.env.STORAGE_PROVIDER || 'local';
    const uniqueId = Date.now().toString(36) + Math.random().toString(36).substring(2);
    const safeUrl = `/uploads/${uniqueId}_${filename || 'label.jpg'}`;

    return {
      url: safeUrl,
      provider: storageProvider,
      uploadedAt: new Date()
    };
  }
}

module.exports = StorageService;
