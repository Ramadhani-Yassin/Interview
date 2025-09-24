import 'dart:convert';
import 'package:crypto/crypto.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:secure_mobile_app/models/user.dart';
import 'package:secure_mobile_app/services/device_info_service.dart';
import 'package:secure_mobile_app/services/local_db.dart';
import 'package:secure_mobile_app/services/secure_storage_service.dart';

class AuthProvider extends ChangeNotifier {
	final SecureStorageService _secureStorageService;
	final LocalDbService _localDbService;
	final DeviceInfoService _deviceInfoService;

	AuthProvider(this._secureStorageService, this._localDbService, this._deviceInfoService);

	UserProfileModel? _currentUser;
	bool _isAuthenticated = false;
	bool _locked = false;

	UserProfileModel? get currentUser => _currentUser;
	bool get isAuthenticated => _isAuthenticated;
	bool get isLocked => _locked;

	Future<void> loadSession() async {
		final email = await _secureStorageService.readString(SecureStorageService.keyEmail);
		if (email != null) {
			final row = await _localDbService.getProfile(email);
			if (row != null) {
				_currentUser = UserProfileModel.fromDb(row);
				_isAuthenticated = true;
				notifyListeners();
			}
		}
	}

	Future<bool> register({required String name, required String email, required String password}) async {
		// Simulate secure credential storage. In real app, send to API over HTTPS.
		await _secureStorageService.writeString(SecureStorageService.keyEmail, email);
		await _secureStorageService.writeString(SecureStorageService.keyAuthToken, _hash(password));
		final deviceId = await _deviceInfoService.getUniqueId();
		await _localDbService.upsertProfile(name: name, email: email, deviceId: deviceId);
		_currentUser = UserProfileModel(name: name, email: email, deviceId: deviceId);
		_isAuthenticated = true;
		notifyListeners();
		return true;
	}

	Future<bool> login({required String email, required String password}) async {
		final storedEmail = await _secureStorageService.readString(SecureStorageService.keyEmail);
		final storedPassHash = await _secureStorageService.readString(SecureStorageService.keyAuthToken);
		if (storedEmail == email && storedPassHash == _hash(password)) {
			final row = await _localDbService.getProfile(email);
			if (row != null) {
				_currentUser = UserProfileModel.fromDb(row);
				_isAuthenticated = true;
				notifyListeners();
				return true;
			}
		}
		return false;
	}

	Future<void> logout() async {
		_isAuthenticated = false;
		_currentUser = null;
		_locked = false;
		notifyListeners();
	}

	Future<bool> loginWithStoredSession() async {
		await loadSession();
		return _isAuthenticated;
	}

	Future<void> lock() async {
		_locked = true;
		notifyListeners();
	}

	Future<void> unlock() async {
		_locked = false;
		notifyListeners();
	}

	String _hash(String input) {
		final bytes = utf8.encode(input);
		return sha256.convert(bytes).toString();
	}
} 