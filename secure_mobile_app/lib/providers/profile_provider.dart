import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:image_picker/image_picker.dart';
import 'package:secure_mobile_app/models/user.dart';
import 'package:secure_mobile_app/services/local_db.dart';

class ProfileProvider extends ChangeNotifier {
	final LocalDbService _localDbService;
	final ImagePicker _picker = ImagePicker();

	UserProfileModel? _profile;
	bool _loading = false;

	ProfileProvider(this._localDbService);

	UserProfileModel? get profile => _profile;
	bool get loading => _loading;

	Future<void> loadProfile(String email) async {
		_loading = true;
		notifyListeners();
		final row = await _localDbService.getProfile(email);
		if (row != null) _profile = UserProfileModel.fromDb(row);
		_loading = false;
		notifyListeners();
	}

	Future<void> updateName(String name) async {
		if (_profile == null) return;
		_profile = _profile!.copyWith(name: name);
		await _localDbService.upsertProfile(
			name: _profile!.name,
			email: _profile!.email,
			photoPath: _profile!.photoPath,
			deviceId: _profile!.deviceId,
		);
		notifyListeners();
	}

	Future<void> pickPhotoFromGallery() async {
		final picked = await _picker.pickImage(source: ImageSource.gallery, imageQuality: 80);
		await _setPhoto(picked);
	}

	Future<void> capturePhoto() async {
		final picked = await _picker.pickImage(source: ImageSource.camera, imageQuality: 80);
		await _setPhoto(picked);
	}

	Future<void> _setPhoto(XFile? file) async {
		if (file == null || _profile == null) return;
		_profile = _profile!.copyWith(photoPath: file.path);
		await _localDbService.upsertProfile(
			name: _profile!.name,
			email: _profile!.email,
			photoPath: _profile!.photoPath,
			deviceId: _profile!.deviceId,
		);
		notifyListeners();
	}
} 