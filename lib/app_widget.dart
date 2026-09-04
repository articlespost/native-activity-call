import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class AppWidget extends StatelessWidget {
  const AppWidget({super.key});
  static const platform = MethodChannel('native_call_test/channel');

  Future<void> _abrirTelaNativa() async {
    try {
      await platform.invokeMethod('openNativeScreen', {'userId': 42});
    } on PlatformException catch (e) {
      debugPrint("Falha: '${e.message}'.");
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(title: const Text('Flutter para MVVM Nativo')),
        body: Center(
          child: ElevatedButton(
            onPressed: _abrirTelaNativa,
            child: const Text('Carregar Perfil (Nativo)'),
          ),
        ),
      ),
    );
  }
}
