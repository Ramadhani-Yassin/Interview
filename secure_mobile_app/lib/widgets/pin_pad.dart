import 'package:flutter/material.dart';

class PinPad extends StatefulWidget {
	final int length;
	final Future<bool> Function(String) onSubmit;
	const PinPad({super.key, this.length = 4, required this.onSubmit});

	@override
	State<PinPad> createState() => _PinPadState();
}

class _PinPadState extends State<PinPad> {
	final TextEditingController _controller = TextEditingController();
	String _error = '';

	void _append(String digit) {
		if (_controller.text.length >= widget.length) return;
		setState(() => _controller.text += digit);
		if (_controller.text.length == widget.length) _submit();
	}

	void _backspace() {
		if (_controller.text.isEmpty) return;
		setState(() => _controller.text = _controller.text.substring(0, _controller.text.length - 1));
	}

	Future<void> _submit() async {
		final ok = await widget.onSubmit(_controller.text);
		if (!ok) {
			setState(() {
				_error = 'Incorrect PIN';
				_controller.clear();
			});
		}
	}

	Widget _dot(bool filled) => Container(
		width: 16,
		height: 16,
		margin: const EdgeInsets.symmetric(horizontal: 6),
		decoration: BoxDecoration(
			shape: BoxShape.circle,
			color: filled ? Colors.black : Colors.black26,
		),
	);

	Widget _key(String label, {VoidCallback? onTap}) => InkWell(
		onTap: onTap ?? () => _append(label),
		child: Container(
			alignment: Alignment.center,
			padding: const EdgeInsets.all(16),
			child: Text(label, style: const TextStyle(fontSize: 24)),
		),
	);

	@override
	Widget build(BuildContext context) {
		final filled = _controller.text.length;
		return Column(
			mainAxisSize: MainAxisSize.min,
			children: [
				Row(mainAxisAlignment: MainAxisAlignment.center, children: List.generate(widget.length, (i) => _dot(i < filled))),
				if (_error.isNotEmpty) Padding(padding: const EdgeInsets.only(top: 8), child: Text(_error, style: const TextStyle(color: Colors.red))),
				const SizedBox(height: 16),
				Wrap(
					alignment: WrapAlignment.center,
					children: [
						for (final key in ['1','2','3','4','5','6','7','8','9']) _key(key),
						SizedBox(width: 80, child: _key('⌫', onTap: _backspace)),
						_key('0'),
						SizedBox(width: 80),
					],
				),
			],
		);
	}
} 