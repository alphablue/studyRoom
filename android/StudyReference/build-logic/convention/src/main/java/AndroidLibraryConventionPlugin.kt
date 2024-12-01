import com.android.build.gradle.LibraryExtension
import com.example.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
//            with(pluginManager) {
//                apply("com.android.library")
//                apply("org.jetbrains.kotlin.android")
//            }

            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.android")


            // library 로 확장 한 것을 확인 가능
            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 34
                testOptions.animationsDisabled = true
            }
        }
    }
}