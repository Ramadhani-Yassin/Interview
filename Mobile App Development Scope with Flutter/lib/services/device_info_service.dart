import 'dart:io';

import 'package:device_info_plus/device_info_plus.dart';

class DeviceInfoService {
	final DeviceInfoPlugin _plugin = DeviceInfoPlugin();

	Future<String> getUniqueId() async {
		try {
			if (Platform.isAndroid) {
				final info = await _plugin.androidInfo;
				return info.id ?? info.fingerprint ?? 'unknown-android';
			}
			if (Platform.isIOS) {
				final info = await _plugin.iosInfo;
				return info.identifierForVendor ?? 'unknown-ios';
			}
			if (Platform.isLinux) {
				final info = await _plugin.linuxInfo;
				return info.machineId ?? 'unknown-linux';
			}
			if (Platform.isMacOS) {
				final info = await _plugin.macOsInfo;
				return info.systemGUID ?? 'unknown-macos';
			}
			if (Platform.isWindows) {
				final info = await _plugin.windowsInfo;
				return info.deviceId;
			}
		} catch (_) {}
		return 'unknown-device';
	}
} 