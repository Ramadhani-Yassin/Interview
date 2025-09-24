import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:secure_mobile_app/providers/auth_provider.dart';
import 'package:secure_mobile_app/screens/profile_screen.dart';
import 'package:secure_mobile_app/services/biometric_service.dart';
import 'package:secure_mobile_app/services/secure_storage_service.dart';

class RegisterScreen extends StatefulWidget {
	const RegisterScreen({super.key});

	@override
	State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
	final _formKey = GlobalKey<FormState>();
	final _nameCtrl = TextEditingController();
	final _emailCtrl = TextEditingController();
	final _passwordCtrl = TextEditingController();
	bool _loading = false;
	bool _biometricAvailable = false;
	bool _biometricEnabled = false;

	final _biometricService = BiometricService();
	final _storage = SecureStorageService();

	@override
	void initState() {
		super.initState();
		_checkBiometric();
	}

	Future<void> _checkBiometric() async {
		final ok = await _biometricService.isHardwareSupported();
		setState(() => _biometricAvailable = ok);
	}

	Future<void> _submit() async {
		if (!_formKey.currentState!.validate()) return;
		setState(() => _loading = true);
		final ok = await Provider.of<AuthProvider>(context, listen: false).register(
			name: _nameCtrl.text.trim(),
			email: _emailCtrl.text.trim(),
			password: _passwordCtrl.text,
		);
		if (ok) {
			bool enableBiometric = _biometricEnabled;
			if (_biometricEnabled) {
				// Attempt biometric enrollment/auth to confirm availability now
				final canBio = await _biometricService.canUseBiometric();
				if (canBio) {
					final authed = await _biometricService.authenticateBiometric(reason: 'Enable biometric login');
					enableBiometric = authed;
					if (!authed && mounted) {
						ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Biometric setup failed. You can enable it later.')));
					}
				} else {
					enableBiometric = false;
					if (mounted) {
						ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Biometric auth not available on this device.')));
					}
				}
			}
			await _storage.setBiometricEnabled(enableBiometric);
		}
		setState(() => _loading = false);
		if (ok && mounted) {
			Navigator.of(context).pushReplacement(MaterialPageRoute(builder: (_) => const ProfileScreen()));
		}
	}

	@override
	Widget build(BuildContext context) {
		return Scaffold(
			appBar: AppBar(title: const Text('Register')),
			body: SafeArea(
				child: Padding(
					padding: const EdgeInsets.all(16),
					child: Form(
						key: _formKey,
						child: SingleChildScrollView(
							child: Column(
								children: [
									TextFormField(
										controller: _nameCtrl,
										decoration: const InputDecoration(labelText: 'Name'),
										validator: (v) => v != null && v.trim().isNotEmpty ? null : 'Enter your name',
									),
									TextFormField(
										controller: _emailCtrl,
										keyboardType: TextInputType.emailAddress,
										decoration: const InputDecoration(labelText: 'Email'),
										validator: (v) => v != null && v.contains('@') ? null : 'Enter a valid email',
									),
									TextFormField(
										controller: _passwordCtrl,
										obscureText: true,
										decoration: const InputDecoration(labelText: 'Password (min 6 chars)'),
										validator: (v) => v != null && v.length >= 6 ? null : 'Enter at least 6 characters',
									),
																		if (_biometricAvailable)
										SwitchListTile(
											title: const Text('Enable biometric unlock'),
											value: _biometricEnabled,
											onChanged: (v) => setState(() => _biometricEnabled = v),
										),
									const SizedBox(height: 16),
									SizedBox(
										width: double.infinity,
										child: ElevatedButton(
											onPressed: _loading ? null : _submit,
											child: _loading ? const CircularProgressIndicator() : const Text('Register'),
										),
									),
								],
							),
						),
					),
				),
			),
		);
	}
} 