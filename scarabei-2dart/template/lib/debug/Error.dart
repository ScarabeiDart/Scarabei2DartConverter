import 'package:scarabei_api/ComponentInstaller.dart';


class Err {

  static ComponentInstaller<ErrorComponent> _componentInstaller = new ComponentInstaller<ErrorComponent>("Error");

  static void installComponent(ErrorComponent component_to_install) {
    _componentInstaller.installComponent(component_to_install);
  }


  static ErrorComponent deInstallCurrentComponent() {
    return _componentInstaller.deInstallComponent();
  }

  static ErrorComponent invoke() {
    if (_componentInstaller.getComponent() == null) {
      throw new StateError("ErrorComponent is not installed");
    }
    return _componentInstaller.invokeComponent();
  }

  static ErrorComponent component() {
    return _componentInstaller.getComponent();
  }


  static void throwNotImplementedYet() {
    invoke().reportNotImplementedYet();
  }


  static void reportError(String message, [Error e]) {
    invoke().reportError(message, e);
  }


}

abstract class ErrorComponent {


//  void reportError (Error e);

  void reportError(String message, [Error e]);

  void reportNotImplementedYet();

//  void reportGCLeak(String msg, Object leakingObject);

//  void reportError (Thread t, Throwable e);


}