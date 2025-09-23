import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:secure_mobile_app/providers/auth_provider.dart';
import 'package:secure_mobile_app/screens/profile_screen.dart';
import 'package:secure_mobile_app/screens/register_screen.dart';
import 'package:secure_mobile_app/services/biometric_service.dart';
import 'package:secure_mobile_app/services/secure_storage_service.dart';

class LoginScreen extends StatefulWidget {
	const LoginScreen({super.key});

	@override
	State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
	final _formKey = GlobalKey<FormState>();
	final _emailCtrl = TextEditingController();
	final _passwordCtrl = TextEditingController();
	bool _loading = false;
	final _biometricService = BiometricService();
	final _storage = SecureStorageService();
	bool _bioLoading = false;

	Future<void> _loginWithFace() async {
		setState(() => _bioLoading = true);
		final enabled = await _storage.isBiometricEnabled();
		final canBio = await _biometricService.canUseBiometric();
		bool ok = false;
		if (enabled && canBio) {
			final authed = await _biometricService.authenticateBiometric(reason: 'Login with biometrics');
			if (authed) {
				ok = await Provider.of<AuthProvider>(context, listen: false).loginWithStoredSession();
			}
		}
		setState(() => _bioLoading = false);
		if (ok && mounted) {
			Navigator.of(context).pushReplacement(MaterialPageRoute(builder: (_) => const ProfileScreen()));
		} else {
			ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Biometric not available or no saved session.')));
		}
	}

	Future<void> _submit() async {
		if (!_formKey.currentState!.validate()) return;
		setState(() => _loading = true);
		final ok = await Provider.of<AuthProvider>(context, listen: false).login(
			email: _emailCtrl.text.trim(),
			password: _passwordCtrl.text,
		);
		setState(() => _loading = false);
		if (ok && mounted) {
			Navigator.of(context).pushReplacement(MaterialPageRoute(builder: (_) => const ProfileScreen()));
		} else {
			ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Invalid credentials')));
		}
	}

	@override
	Widget build(BuildContext context) {
		return Scaffold(
			appBar: AppBar(title: const Text('Login')),
			body: SafeArea(
				child: Padding(
					padding: const EdgeInsets.all(16),
					child: Form(
						key: _formKey,
						child: Column(
							children: [
								TextFormField(
									controller: _emailCtrl,
									keyboardType: TextInputType.emailAddress,
									decoration: const InputDecoration(labelText: 'Email'),
									validator: (v) => v != null && v.contains('@') ? null : 'Enter a valid email',
								),
								TextFormField(
									controller: _passwordCtrl,
									obscureText: true,
									decoration: const InputDecoration(labelText: 'Password'),
									validator: (v) => v != null && v.length >= 6 ? null : 'Min 6 characters',
								),
								const SizedBox(height: 16),
								SizedBox(
									width: double.infinity,
									child: ElevatedButton(
										onPressed: _loading ? null : _submit,
										child: _loading ? const CircularProgressIndicator() : const Text('Login'),
									),
								),
								const SizedBox(height: 8),
								SizedBox(
									width: double.infinity,
									child: OutlinedButton.icon(
										onPressed: _bioLoading ? null : _loginWithFace,
										icon: const Icon(Icons.face),
										label: _bioLoading ? const CircularProgressIndicator() : const Text('Login with biometrics'),
									),
								),
								TextButton(
									onPressed: () => Navigator.of(context).push(MaterialPageRoute(builder: (_) => const RegisterScreen())),
									child: const Text('Create an account'),
								),
							],
						),
					),
				),
			),
		);
	}
} 