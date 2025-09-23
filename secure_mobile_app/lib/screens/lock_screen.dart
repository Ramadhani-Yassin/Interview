import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:secure_mobile_app/providers/auth_provider.dart';
import 'package:secure_mobile_app/services/biometric_service.dart';
import 'package:secure_mobile_app/services/secure_storage_service.dart';
import 'package:secure_mobile_app/screens/login_screen.dart';

class LockScreen extends StatefulWidget {
	const LockScreen({super.key});

	@override
	State<LockScreen> createState() => _LockScreenState();
}

class _LockScreenState extends State<LockScreen> {
	final BiometricService _biometrics = BiometricService();
	final SecureStorageService _storage = SecureStorageService();
	bool _authTried = false;
	bool _authFailed = false;

	@override
	void initState() {
		super.initState();
		_unlockWithFace();
	}

	Future<void> _unlockWithFace() async {
		final enabled = await _storage.isBiometricEnabled();
		if (enabled && await _biometrics.canUseBiometric()) {
			final ok = await _biometrics.authenticateBiometric(reason: 'Unlock with biometrics');
			if (ok && mounted) {
				Provider.of<AuthProvider>(context, listen: false).unlock();
				return;
			}
		}
		setState(() {
			_authTried = true;
			_authFailed = true;
		});
	}

	@override
	Widget build(BuildContext context) {
		return Scaffold(
			body: SafeArea(
				child: Center(
					child: Padding(
						padding: const EdgeInsets.all(24),
						child: Column(
							mainAxisSize: MainAxisSize.min,
							children: [
								const Icon(Icons.lock, size: 64),
								const SizedBox(height: 12),
								const Text('App Locked', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
								const SizedBox(height: 4),
								const Text('Authenticate with biometrics'),
								const SizedBox(height: 24),
								if (!_authTried) const CircularProgressIndicator(),
								if (_authTried) ...[
									if (_authFailed)
										const Text('Biometrics failed or unavailable', style: TextStyle(color: Colors.red)),
									const SizedBox(height: 12),
									ElevatedButton.icon(
										onPressed: _unlockWithFace,
										icon: const Icon(Icons.fingerprint),
										label: const Text('Try again'),
									),
									const SizedBox(height: 8),
									TextButton(
										onPressed: () async {
											await Provider.of<AuthProvider>(context, listen: false).logout();
											if (mounted) {
												Navigator.of(context).pushAndRemoveUntil(
													MaterialPageRoute(builder: (_) => const LoginScreen()),
													(route) => false,
												);
											}
										},
										child: const Text('Sign out and login'),
									),
								],
							],
						),
					),
				),
			),
		);
	}
} 