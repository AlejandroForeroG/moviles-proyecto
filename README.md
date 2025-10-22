# Vynils - Aplicación Android con Clean Architecture

## Descripción

Vynils es una aplicación Android desarrollada con las mejores prácticas de arquitectura moderna, implementando Clean Architecture, patrón Repository, y principios SOLID para crear una aplicación mantenible, escalable y testeable.

## Arquitectura del Proyecto

### Configuración General

Para reemplazar la url del API, modifica la constante `BASE_URL` en el archivo `build.gradle` del módulo `app`:

```gradle
buildConfigField("String", "BASE_URL", "\"https://nueva-url-del-api.com"")
``` 

### Clean Architecture con MVVM

El proyecto está estructurado en capas claramente definidas:

```
app/
├── data/              # Capa de datos
│   ├── database/      # Room database, DAOs y Entities
│   ├── model/         # Modelos de datos
│   └── repository/    # Implementación de repositorios
├── domain/            # Capa de dominio
│   └── usecase/       # Casos de uso (lógica de negocio)
├── di/                # Dependency Injection con Hilt
└── ui/                # Capa de presentación
    ├── user/          # Pantalla de selección de usuario
    ├── album/         # Pantalla de álbumes
    └── MainViewModel  # ViewModel principal
```

### Patrón Repository

El **patrón Repository** actúa como una capa de abstracción entre la capa de dominio y la capa de datos. Proporciona una API limpia para acceder a los datos sin que la lógica de negocio necesite conocer los detalles de implementación.

#### Beneficios:
- **Separación de responsabilidades**: La lógica de negocio no depende de la fuente de datos
- **Facilita el testing**: Se pueden crear repositorios mock fácilmente
- **Centralización**: Un único punto de acceso a los datos
- **Flexibilidad**: Fácil cambiar la fuente de datos (Room, API, SharedPreferences) sin afectar el resto de la app

#### Ejemplo en el proyecto:

```kotlin
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun saveUser(userType: String) {
        userDao.insertUser(UserEntity(id = 1, userType = userType))
    }

    fun getUser(): UserEntity? {
        return userDao.getUser()
    }

    suspend fun clearUser() {
        userDao.clearUser()
    }
}
```

### Use Cases (Casos de Uso)

Los **Use Cases** encapsulan la lógica de negocio específica de la aplicación. Cada Use Case representa una única acción que el usuario puede realizar.

#### Beneficios:
- **Single Responsibility Principle**: Cada caso de uso tiene una única responsabilidad
- **Reutilización**: Los casos de uso pueden ser reutilizados en diferentes ViewModels
- **Testabilidad**: Fáciles de probar de manera aislada
- **Legibilidad**: El código es más expresivo y fácil de entender
- **Mantenibilidad**: Cambios en la lógica de negocio están aislados

#### Ejemplos en el proyecto:

```kotlin
// Guardar usuario
class SaveUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userType: String) {
        repository.saveUser(userType)
    }
}

// Obtener usuario
class GetUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): UserEntity? {
        return repository.getUser()
    }
}

// Limpiar usuario
class ClearUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke() {
        repository.clearUser()
    }
}
```

Los ViewModels solo invocan estos casos de uso sin conocer los detalles de implementación:

```kotlin
@HiltViewModel
class UserViewModel @Inject constructor(
    private val saveUserUseCase: SaveUserUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {
    
    fun saveUser(userType: String) {
        viewModelScope.launch {
            saveUserUseCase(userType)
            _gotoMain.value = true
        }
    }
}
```

## 💉 Hilt - Dependency Injection

**Hilt** es el framework de inyección de dependencias recomendado por Google para Android. Está construido sobre Dagger y proporciona una forma estándar de implementar DI en aplicaciones Android.

### ¿Por qué Hilt?

#### 1. **Reduce el Boilerplate**
- Eliminación de factory patterns manuales
- No más `new` instances en todo el código
- Configuración automática de componentes Android

#### 2. **Facilita el Testing**
- Permite reemplazar dependencias fácilmente en tests
- Provee `HiltAndroidTest` para testing instrumentado
- Simplifica la creación de mocks y fakes

#### 3. **Lifecycle-Aware**
- Maneja automáticamente el ciclo de vida de las dependencias
- Scopes apropiados para Activities, Fragments, ViewModels, etc.

#### 4. **Type Safety**
- Errores de compilación en lugar de runtime
- Detección temprana de problemas de configuración

### Configuración de Hilt en el Proyecto

#### 1. Application Class
```kotlin
@HiltAndroidApp
class VynilsApplication : Application()
```

#### 2. Módulos de Hilt
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "vynils_database"
        ).build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}
```

#### 3. Inyección en Activities y ViewModels
```kotlin
@AndroidEntryPoint
class UserActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModels()
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val saveUserUseCase: SaveUserUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel()
```

## 🗄️ Room - Persistencia de Datos

**Room** es una biblioteca de persistencia que proporciona una capa de abstracción sobre SQLite, permitiendo un acceso fluido a la base de datos mientras aprovecha todo el poder de SQLite.

### Componentes de Room

#### 1. **Entity (Entidad)**
Define la estructura de la tabla en la base de datos:

```kotlin
@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val userType: String
)
```

#### 2. **DAO (Data Access Object)**
Define las operaciones de base de datos:

```kotlin
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM user LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Query("DELETE FROM user")
    suspend fun clearUser()
}
```

#### 3. **Database**
La clase que contiene la base de datos:

```kotlin
@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
```

### Beneficios de Room

- **Verificación en tiempo de compilación**: Detecta errores SQL en compile-time
- **Menos boilerplate**: Reduce código repetitivo comparado con SQLite directo
- **Integración con LiveData/Flow**: Observación reactiva de cambios en la BD
- **Migraciones**: Sistema robusto para actualizar esquemas de base de datos
- **Testing**: Fácil de probar con bases de datos en memoria

## 🧪 Testing

### Importancia de Hilt en Testing

Hilt facilita enormemente el testing al permitir:

1. **Reemplazo de dependencias reales por mocks**
2. **Inyección automática en tests**
3. **Aislamiento de componentes**

### Test Instrumentados con Hilt

```kotlin
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class UserInstrumentedTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var activityRule = ActivityScenarioRule(UserActivity::class.java)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun clickingUsuarioNavigatesToMain() {
        onView(withId(R.id.btnUsuario)).perform(click())
        // Verifica navegación a MainActivity
        intended(hasComponent(MainActivity::class.java.name))
    }
}
```

### Tests Unitarios con Robolectric

```kotlin
@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
class UserTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun clickingUsuarioNavigatesToMain() {
        val scenario = launchActivity<UserActivity>()
        
        onView(withId(R.id.btnUsuario)).perform(click())
        
        val nextIntent = shadowOf(RuntimeEnvironment.application).nextStartedActivity
        assertThat(nextIntent).isNotNull()
        assertThat(nextIntent.component?.className)
            .isEqualTo(MainActivity::class.java.name)
    }
}
```

## Tecnologías y Librerías

- **Kotlin**: Lenguaje de programación principal
- **Hilt**: Inyección de dependencias
- **Room**: Persistencia de datos local
- **Coroutines**: Programación asíncrona
- **ViewModel**: Gestión de UI state
- **LiveData**: Observación de datos lifecycle-aware
- **Navigation Component**: Navegación entre pantallas
- **View Binding**: Binding de vistas type-safe
- **Espresso**: Testing instrumentado
- **Robolectric**: Testing unitario con contexto Android

## 📦 Configuración del Proyecto

### Requisitos
- JDK 17
- Gradle 8.0+
- Android SDK 34

### Instalación

1. Clonar el repositorio
```bash
git clone https://github.com/AlejandroForeroG/moviles-proyecto
```

2. Abrir el proyecto en Android Studio

3. Sincronizar Gradle
```bash
./gradlew sync
```

4. Ejecutar la aplicación
```bash
./gradlew installDebug
```

## 🧪 Ejecutar Tests

### Tests Unitarios (Robolectric)
```bash
./gradlew test
```

### Tests Instrumentados (Espresso)
```bash
./gradlew connectedAndroidTest
```
