class UserProfileModel {
	final String name;
	final String email;
	final String? photoPath;
	final String? deviceId;

	UserProfileModel({required this.name, required this.email, this.photoPath, this.deviceId});

	UserProfileModel copyWith({String? name, String? email, String? photoPath, String? deviceId}) {
		return UserProfileModel(
			name: name ?? this.name,
			email: email ?? this.email,
			photoPath: photoPath ?? this.photoPath,
			deviceId: deviceId ?? this.deviceId,
		);
	}

	factory UserProfileModel.fromDb(Map<String, Object?> row) {
		return UserProfileModel(
			name: (row['name'] as String?) ?? '',
			email: (row['email'] as String?) ?? '',
			photoPath: row['photo_path'] as String?,
			deviceId: row['device_id'] as String?,
		);
	}
} 