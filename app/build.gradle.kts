plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.oms.omsguru"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.oms.omsguru"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "v1.13"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    //Sdp Library
    implementation("com.intuit.sdp:sdp-android:1.1.0")

    //Lottie Animation
    implementation("com.airbnb.android:lottie:3.4.0")

    //Otp Verify view
    implementation("io.github.chaosleung:pinview:1.4.4")

    //Circuler Imageview
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //QR Scanner
    implementation("com.github.murgupluoglu:qrreader-android:6.3.0")

    //Dexter for run time permissions
    implementation("com.karumi:dexter:6.2.3")

    //Material Spinner
    implementation("com.jaredrummler:material-spinner:1.3.1")
    implementation("com.github.MarsadMaqsood:StylishDialogs:0.1.+")

    //Spinner
    implementation("com.github.chivorns:smartmaterialspinner:2.0.0")

    // Retrofit")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.3")

    //Volly
    implementation("com.android.volley:volley:1.2.1")

    //Dhaval image Picker
    implementation("com.github.dhaval2404:imagepicker:2.1")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //Searchable Spinner
    implementation("com.github.leoncydsilva:SearchableSpinner:1.0.1")

    //FFmpeg video compressing
    implementation("com.arthenica:mobile-ffmpeg-full:4.4")

    //Fireabse
    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")

}
