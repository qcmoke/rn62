import React, { useEffect } from "react";
import { NativeModules, SafeAreaView, ScrollView, StatusBar, Text, TouchableOpacity } from "react-native";

import AsyncStorage from "@react-native-community/async-storage";
import { compareVersion } from "./src/utils/versionUtil";
import { fetchApi } from "./src/utils/fetchUtil";

const App = (props) => {

  useEffect(() => {
    //let androidBundleVersion = jsBundleVersion.androidBundleVersion;
    fetchApi("http://192.168.137.1:8089/jsBundle/jsBundleVersion.json?m=" + Math.random(), "", {}).then(async rs => {
      const localBundleVersion = await AsyncStorage.getItem("localBundleVersion");
      let lastBundleVersion = rs["androidBundleVersion"];
      let androidBundleUrl = rs["androidBundleUrl"];
      console.log("===localBundleVersion,lastBundleVersion==", localBundleVersion, lastBundleVersion);
      if (!localBundleVersion || compareVersion(localBundleVersion, lastBundleVersion) < 0) {
        NativeModules.RNJSMainModule.updateJsBundleFile({ androidBundleUrl: androidBundleUrl }).then(rs => {
          console.log("====更新jsBundle文件成功");
          AsyncStorage.setItem("localBundleVersion", lastBundleVersion);
          NativeModules.RNJSMainModule.restartAPP().then(rs => {
            console.log("重启APP成功");
          }).catch(err => {
            console.log(err);
          });
        }).catch(err => {
          console.log("===err", err);
        });
      }
    }).catch(err => {
      console.log("===err", err);
    });

  }, []);
  return (
    <>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView>
        <ScrollView
          contentInsetAdjustmentBehavior="automatic"
          style={{ backgroundColor: "gary" }}>
          <TouchableOpacity
            style={{ height: 200, width: 200, backgroundColor: "red" }}
            onPress={() => {
            }}>
            <Text>456</Text>
          </TouchableOpacity>
        </ScrollView>
      </SafeAreaView>
    </>
  );
};

export default App;
