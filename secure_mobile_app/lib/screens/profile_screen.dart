import 'dart:io';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:secure_mobile_app/providers/auth_provider.dart';
import 'package:secure_mobile_app/providers/profile_provider.dart';
import 'package:secure_mobile_app/screens/lock_screen.dart';
import 'package:secure_mobile_app/screens/login_screen.dart';

class ProfileScreen extends StatefulWidget {
	const ProfileScreen({super.key});

	@override
	State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> with WidgetsBindingObserver {
	final _nameCtrl = TextEditingController();

	@override
	void initState() {
		super.initState();
		WidgetsBinding.instance.addObserver(this);
		final auth = Provider.of<AuthProvider>(context, listen: false);
		if (auth.currentUser != null) {
			Provider.of<ProfileProvider>(context, listen: false).loadProfile(auth.currentUser!.email);
		}
	}

	@override
	void dispose() {
		WidgetsBinding.instance.removeObserver(this);
		super.dispose();
	}

	@override
	void didChangeAppLifecycleState(AppLifecycleState state) {
		if (state == AppLifecycleState.paused || state == AppLifecycleState.detached) {
			Provider.of<AuthProvider>(context, listen: false).lock();
		}
	}

	@override
	Widget build(BuildContext context) {
		final auth = Provider.of<AuthProvider>(context);
		final profileProvider = Provider.of<ProfileProvider>(context);
		final profile = profileProvider.profile;

		if (auth.isLocked) return const LockScreen();

		return Scaffold(
			appBar: AppBar(
				title: const Text('Profile'),
				actions: [
					IconButton(
						onPressed: () async {
							await auth.logout();
							if (mounted) {
								Navigator.of(context).pushAndRemoveUntil(
									MaterialPageRoute(builder: (_) => const LoginScreen()),
									(route) => false,
								);
							}
						},
						icon: const Icon(Icons.logout),
					),
				],
			),
			body: SafeArea(
				child: profileProvider.loading
						? const Center(child: CircularProgressIndicator())
						: Padding(
							padding: const EdgeInsets.all(16),
							child: Column(
								crossAxisAlignment: CrossAxisAlignment.start,
								children: [
									Row(
										children: [
											CircleAvatar(
												radius: 40,
												backgroundImage: (profile?.photoPath != null && File(profile!.photoPath!).existsSync()) ? FileImage(File(profile.photoPath!)) : null,
												child: (profile?.photoPath == null) ? const Icon(Icons.person, size: 40) : null,
											),
											const SizedBox(width: 16),
											Expanded(
												child: Column(
													crossAxisAlignment: CrossAxisAlignment.start,
													children: [
														Text(profile?.name ?? '', style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
														Text(profile?.email ?? ''),
													],
												),
											),
										],
									),
									const SizedBox(height: 16),
									Text('Device ID: ${profile?.deviceId ?? ''}', style: const TextStyle(fontSize: 12, color: Colors.grey)),
									const SizedBox(height: 24),
									TextField(
										controller: _nameCtrl..text = profile?.name ?? '',
										decoration: const InputDecoration(labelText: 'Update Name'),
									),
									const SizedBox(height: 8),
									Row(
										children: [
											ElevatedButton.icon(
												onPressed: () => profileProvider.capturePhoto(),
												icon: const Icon(Icons.camera_alt),
												label: const Text('Camera'),
											),
											const SizedBox(width: 8),
											ElevatedButton.icon(
												onPressed: () => profileProvider.pickPhotoFromGallery(),
												icon: const Icon(Icons.photo),
												label: const Text('Gallery'),
											),
										],
									),
									const Spacer(),
									SizedBox(
										width: double.infinity,
										child: ElevatedButton(
											onPressed: () async {
												await profileProvider.updateName(_nameCtrl.text.trim());
												if (mounted) {
													ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Profile updated')));
												}
											},
											child: const Text('Save'),
										),
									),
								],
							),
						),
				),
		);
	}
} 