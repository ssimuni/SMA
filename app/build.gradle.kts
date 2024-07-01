plugins {
    id("com.android.application")
    id("com.apollographql.apollo") version "2.5.9"
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.simu"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.simu"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments(
                    mapOf(
                        "apollo.schema.file" to "$projectDir/src/main/graphql/com/example/simu/schema.json"
                    )
                )
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }


    buildFeatures{
        viewBinding = true
        mlModelBinding = true
        true.also { dataBinding = it }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-firestore:24.10.3")
    implementation ("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.1.0")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-storage")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("commons-net:commons-net:3.8.0")
    implementation ("com.jakewharton.threetenabp:threetenabp:1.3.1")

    implementation ("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation ("io.reactivex.rxjava2:rxjava:2.2.6")

    implementation ("com.apollographql.apollo:apollo-rx2-support:2.5.9")
    implementation ("com.apollographql.apollo:apollo-runtime:2.5.9")
}

apollo {
    service("countries") {
        sourceFolder.set("com/example/simu")
        rootPackageName.set("com.example.simu")
        schemaFile.set(file("src/main/graphql/com/example/simu/schema.graphqls"))
        introspection {
            endpointUrl.set("https://countries.trevorblades.com/graphql")
        }
    }
}