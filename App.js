import CodePush from "react-native-code-push";
import React, { useEffect } from "react";
import { Alert, SafeAreaView, ScrollView, StatusBar, Text, View } from "react-native";

import { Colors } from "react-native/Libraries/NewAppScreen";


const App = (props) => {

  useEffect(() => {
    //https://github.com/microsoft/react-native-code-push/blob/master/docs/api-js.md
    CodePush.checkForUpdate().then(update => {
      if (!update) {
        console.log("已是最新版本");
        return;
      }
      /*
      CodePushNativeModule
          downloadUpdate=>mUpdateManager.downloadPackage
          package-mixins.js=>install=>NativeCodePush.installUpdate=>mUpdateManager.installPackage。（如立即安装则监听Activity生命周期onHostResume后执行restartAppInternal加装安装js，NativeCodePush.restartApp(false)）

       */
      CodePush.sync({
        //deploymentKey: "deployment-key-here",
        updateDialog: {
          optionalIgnoreButtonLabel: "稍后",
          optionalInstallButtonLabel: "后台更新",
          optionalUpdateMessage: "有新版本了，是否更新？",
          title: "更新提示",
        },
        installMode: CodePush.InstallMode.IMMEDIATE,
        /*
        installMode: CodePush.InstallMode.ON_NEXT_RESTART,//启动模式三种：ON_NEXT_RESUME、ON_NEXT_RESTART、IMMEDIATE
        updateDialog: false,   // 苹果公司和中国区安卓的热更新，是不允许弹窗提示的，所以不能设置为true
        */
      }).then(status => {
        switch (status) {
          case CodePush.SyncStatus.CHECKING_FOR_UPDATE:
            console.log("Checking for updates.");
            break;
          case CodePush.SyncStatus.DOWNLOADING_PACKAGE:
            console.log("Downloading package.");
            break;
          case CodePush.SyncStatus.INSTALLING_UPDATE:
            console.log("Installing update.");
            break;
          case CodePush.SyncStatus.UP_TO_DATE:
            console.log("Up-to-date.");
            break;
          case CodePush.SyncStatus.UPDATE_INSTALLED:
            console.log("Update installed.");
            break;
        }
      }).catch(err => {
        console.log("=======err====", err);
      });
    }).catch(err => {
      console.log("=======err====", err);
    });

  }, []);
  return (
    <>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView>
        <ScrollView
          contentInsetAdjustmentBehavior="automatic"
          style={{ backgroundColor: Colors.lighter }}>
          <View>
            <Text>456</Text>
          </View>
        </ScrollView>
      </SafeAreaView>
    </>
  );
};

let codePushOptions = { checkFrequency: CodePush.CheckFrequency.MANUAL };
export default CodePush(codePushOptions)(App);
