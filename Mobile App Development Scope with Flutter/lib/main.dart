import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:secure_mobile_app/providers/auth_provider.dart';
import 'package:secure_mobile_app/providers/profile_provider.dart';
import 'package:secure_mobile_app/screens/lock_screen.dart';
import 'package:secure_mobile_app/screens/login_screen.dart';
import 'package:secure_mobile_app/screens/profile_screen.dart';
import 'package:secure_mobile_app/screens/register_screen.dart';
import 'package:secure_mobile_app/services/device_info_service.dart';
import 'package:secure_mobile_app/services/local_db.dart';
import 'package:secure_mobile_app/services/secure_storage_service.dart';

void main() {
	runApp(const MyApp());
}

class MyApp extends StatefulWidget {
	const MyApp({super.key});

	@override
	State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> with WidgetsBindingObserver {
	late final SecureStorageService _secureStorageService;
	late final LocalDbService _localDbService;
	late final DeviceInfoService _deviceInfoService;

	@override
	void initState() {
		super.initState();
		WidgetsBinding.instance.addObserver(this);
		_secureStorageService = SecureStorageService();
		_localDbService = LocalDbService();
		_deviceInfoService = DeviceInfoService();
	}

	@override
	void dispose() {
		WidgetsBinding.instance.removeObserver(this);
		super.dispose();
	}

	@override
	void didChangeAppLifecycleState(AppLifecycleState state) {
		if (state == AppLifecycleState.paused || state == AppLifecycleState.detached) {
			final auth = Provider.of<AuthProvider>(context, listen: false);
			auth.lock();
		}
	}

	@override
	Widget build(BuildContext context) {
		return MultiProvider(
			providers: [
				ChangeNotifierProvider(create: (_) {
					final auth = AuthProvider(_secureStorageService, _localDbService, _deviceInfoService);
					auth.loadSession().then((_) {
						if (auth.isAuthenticated) {
							auth.lock();
						}
					});
					return auth;
				}),
				ChangeNotifierProvider(create: (_) => ProfileProvider(_localDbService)),
			],
			child: Consumer<AuthProvider>(
				builder: (context, auth, _) {
					Widget home;
					if (auth.isLocked) {
						home = const LockScreen();
					} else if (auth.isAuthenticated) {
						home = const ProfileScreen();
					} else {
						home = const LoginScreen();
					}
					return MaterialApp(
						title: 'Secure App',
						theme: ThemeData(useMaterial3: true, colorSchemeSeed: Colors.blue),
						debugShowCheckedModeBanner: false,
						home: home,
					);
				},
			),
		);
	}
}
