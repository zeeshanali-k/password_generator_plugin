package app.keyboardly.addon.passwordgenerator.di

import app.keyboardly.lib.reflector.DynamicFeature
import app.keyboardly.lib.KeyboardActionDependency
import app.keyboardly.addon.passwordgenerator.DynamicFeatureImpl
import app.keyboardly.addon.passwordgenerator.PasswordGeneratorDefaultClass
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * This component for build the default class
 */
@Singleton
@Component(
    modules = [DynamicModule::class],
    dependencies = [DynamicFeature.Dependencies::class] // needs dependencies passed in to create component
)
interface DynamicComponent {
    fun dynamicFeature(): DynamicFeature
}

/**
 * You have access to objects provided by the DynamicFeature.Dependencies interface from the base component.
 */
@Module
class DynamicModule {
    /**
     * fit with name default class
     * @param dependency
     * @return the default class
     */
    @Provides
    internal fun provideDefaultClass(dependency: KeyboardActionDependency) = PasswordGeneratorDefaultClass(dependency)

    /**
     * bind the DynamicFeatureImpl
     * @param featureImpl: DynamicFeatureImpl
     */
    @Provides
    internal fun bindDynamicImpl(featureImpl: DynamicFeatureImpl): DynamicFeature = featureImpl
}