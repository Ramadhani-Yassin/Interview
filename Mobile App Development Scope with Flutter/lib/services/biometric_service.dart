import 'package:local_auth/local_auth.dart';

class BiometricService {
	final LocalAuthentication _auth = LocalAuthentication();

	Future<bool> canCheckBiometrics() async {
		try {
			final canCheck = await _auth.canCheckBiometrics;
			final isSupported = await _auth.isDeviceSupported();
			return canCheck && isSupported;
		} catch (_) {
			return false;
		}
	}

	Future<bool> canUseBiometric() async {
		try {
			final types = await _auth.getAvailableBiometrics();
			return types.isNotEmpty;
		} catch (_) {
			return false;
		}
	}

	Future<bool> authenticateBiometric({String reason = 'Authenticate'}) async {
		try {
			final can = await canUseBiometric();
			if (!can) return false;
			return await _auth.authenticate(
				localizedReason: reason,
				options: const AuthenticationOptions(
					biometricOnly: true,
					stickyAuth: true,
					sensitiveTransaction: true,
				),
			);
		} catch (_) {
			return false;
		}
	}

	// Backward-compatible face-specific helpers
	Future<bool> canUseFace() async {
		try {
			final types = await _auth.getAvailableBiometrics();
			return types.contains(BiometricType.face);
		} catch (_) {
			return false;
		}
	}

	Future<bool> authenticateFace({String reason = 'Authenticate with Face ID'}) async {
		return authenticateBiometric(reason: reason);
	}

	Future<bool> isHardwareSupported() async {
		try {
			return await _auth.isDeviceSupported();
		} catch (_) {
			return false;
		}
	}
} 