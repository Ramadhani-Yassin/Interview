import 'dart:async';

import 'package:path/path.dart' as p;
import 'package:path_provider/path_provider.dart';
import 'package:sqflite/sqflite.dart';

class LocalDbService {
	static final LocalDbService _instance = LocalDbService._internal();
	factory LocalDbService() => _instance;
	LocalDbService._internal();

	Database? _db;

	Future<Database> get database async {
		if (_db != null) return _db!;
		_db = await _initDb();
		return _db!;
	}

	Future<Database> _initDb() async {
		final dir = await getApplicationDocumentsDirectory();
		final path = p.join(dir.path, 'app.db');
		return openDatabase(
			path,
			version: 1,
			onCreate: (db, version) async {
				await db.execute('''
					CREATE TABLE user_profile(
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						name TEXT,
						email TEXT UNIQUE,
						photo_path TEXT,
						device_id TEXT
					);
				''');
			},
		);
	}

	Future<Map<String, Object?>?> getProfile(String email) async {
		final db = await database;
		final rows = await db.query('user_profile', where: 'email = ?', whereArgs: [email], limit: 1);
		return rows.isNotEmpty ? rows.first : null;
	}

	Future<void> upsertProfile({required String name, required String email, String? photoPath, String? deviceId}) async {
		final db = await database;
		await db.insert(
			'user_profile',
			{'name': name, 'email': email, 'photo_path': photoPath, 'device_id': deviceId},
			conflictAlgorithm: ConflictAlgorithm.replace,
		);
	}
} 