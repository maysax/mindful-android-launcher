FROM openjdk:8-jdk

ENV ANDROID_COMPILE_SDK 25
ENV ANDROID_BUILD_TOOLS 24.0.0
ENV ANDROID_SDK_TOOLS 24.4.1
ENV GRADLE_VER 2.14.1

RUN apt-get --quiet update --yes
RUN apt-get --quiet install --yes wget tar unzip lib32stdc++6 lib32z1
RUN wget --quiet --output-document=android-sdk.tgz https://dl.google.com/android/android-sdk_r${ANDROID_SDK_TOOLS}-linux.tgz
RUN tar --extract --gzip --file=android-sdk.tgz
RUN echo y | android-sdk-linux/tools/android --silent update sdk --no-ui --all --filter android-${ANDROID_COMPILE_SDK}
RUN echo y | android-sdk-linux/tools/android --silent update sdk --no-ui --all --filter platform-tools
RUN echo y | android-sdk-linux/tools/android --silent update sdk --no-ui --all --filter build-tools-${ANDROID_BUILD_TOOLS}
RUN echo y | android-sdk-linux/tools/android --silent update sdk --no-ui --all --filter extra-android-m2repository
RUN echo y | android-sdk-linux/tools/android --silent update sdk --no-ui --all --filter extra-google-google_play_services
RUN echo y | android-sdk-linux/tools/android --silent update sdk --no-ui --all --filter extra-google-m2repository

RUN wget https://services.gradle.org/distributions/gradle-$GRADLE_VER-all.zip
RUN mkdir /opt/gradle
RUN unzip -d /opt/gradle gradle-$GRADLE_VER-all.zip
RUN export PATH=$PATH:/opt/gradle/gradle-$GRADLE_VER/bin
#RUN gradle -v



RUN export ANDROID_HOME=$PWD/android-sdk-linux
RUN export PATH=$PATH:$PWD/android-sdk-linux/platform-tools/
#RUN chmod +x ./gradlew
CMD /bin/bash
