import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class SecureStorageService {
	static const _storage = FlutterSecureStorage(
			iOptions: IOSOptions(accessibility: KeychainAccessibility.first_unlock_this_device),
			aOptions: AndroidOptions(encryptedSharedPreferences: true),
	);

	// Keys
	static const String keyAuthToken = 'auth_token';
	static const String keyEmail = 'user_email';
	static const String keyHashedPin = 'user_pin_hash';
	static const String keyBiometricEnabled = 'biometric_enabled';

	Future<void> writeString(String key, String value) async {
		await _storage.write(key: key, value: value);
	}

	Future<String?> readString(String key) async {
		return _storage.read(key: key);
	}

	Future<void> deleteKey(String key) async {
		await _storage.delete(key: key);
	}

	Future<void> clearAll() async {
		await _storage.deleteAll();
	}

	Future<void> setBiometricEnabled(bool enabled) async {
		await writeString(keyBiometricEnabled, enabled ? '1' : '0');
	}

	Future<bool> isBiometricEnabled() async {
		final v = await readString(keyBiometricEnabled);
		return v == '1';
	}
} 