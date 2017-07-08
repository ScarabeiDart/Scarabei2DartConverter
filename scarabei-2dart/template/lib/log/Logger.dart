import 'package:scarabei_api/ComponentInstaller.dart';


class L extends Logger {
}

class Logger {

  ComponentInstaller<LoggerComponent> _componentInstaller = new ComponentInstaller<LoggerComponent>("Logger");


  void installComponent(LoggerComponent component_to_install) {
    _componentInstaller.installComponent(component_to_install);
  }

  LoggerComponent deInstallCurrentComponent() {
    return _componentInstaller.deInstallComponent();
  }

  LoggerComponent invoke() {
    return _componentInstaller.invokeComponent();
  }

  LoggerComponent component() {
    return _componentInstaller.getComponent();
  }
}

abstract class LoggerComponent {

  d(Object tag, [Object msg]); //debug

  e(Object tag, [Object msg]); //error

}