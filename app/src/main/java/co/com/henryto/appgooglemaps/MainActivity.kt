package co.com.henryto.appgooglemaps

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

//Bibliografia: https://www.youtube.com/watch?v=eDWDlgo_3TM&list=PL8ie04dqq7_OcBYDpvHrcSFVoggLi3cm_&index=34
//              https://cursokotlin.com/polylines-en-google-maps-con-kotlin-parte-3--capitulo-37/
//Vamos en 10 min y 30 seg

/* Para implemetar google Maps, en el gradle agregamos la dependencia
implementation 'com.google.android.gms:play-services-maps:17.0.0',  tambien creamos el recurso
string "res/values/google_maps_api.xml" y tambien agregamos el meta-data correspondiente en el
fichero AndroidManifest.xml

para implementar la geolocalización en tiempo real del usuario, necesitamos solicitar el permiso de localizacion,
y lo hacemos en el fichero AndroidManifest.xml con =>  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
*/


class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener{ // extendemos de la interfaz OnMapReadyCallback
    // lo que implica implementar su metodo "onMapReady" por ser una interfaz, tambien extendemos de GoogleMap.OnMyLocationClickListener
    // lo que implica implementar su metodo "onMyLocationButtonClick" por ser una interfaz, tambien extendemos de GoogleMap.OnMyLocationClickListener
    // lo que implica omplementar su metodo "onMyLocationClick" por ser una interfaz.

    private lateinit var map: GoogleMap // creamos la variable map que es de tipo GoogleMap al que mas adelante
    // le asignaremos el mapa (en la función onMapReady) que sera creado.

companion object{
    const val REQUEST_CODE_LOCATION = 3461   // esta es el codigo que vamos a usar en requestLocationPermission()
                                    // asociado a los permisos estamos solicitando
}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createMapFragment()
    }

    private fun createMapFragment() {
        //Esta función crea el mapa
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        // creamos la variable mapFragment y le decimos a SupportMapFragment que busque un componente de tipo fragment en nuestro
        // layout que tenga el id fragmentMap.. en este punto, la variable mapFragment es un
        // fragment normal y debemos convertilo a un fragment de tipo mapa, es decir de tipo SupportMapFragment, por tal motivo
        // al final ponemos " as SupportMapFragment"
        mapFragment.getMapAsync(this)  // inicializamos nuestro fragment con el mapa con el metodo getMapAsync(this). en este momento
        // el sistema nos muestra error por que nos exige implementar y sobreescribir el metodo onMapReady y para ello es que a
        // debemos extender la clase MainActivity con la interfaz OnMapReadyCallback
    }

    override fun onMapReady(googleMap: GoogleMap?) { // este es un metodo de hace parte de la interfaz OnMapReadyCallback que usamos
        // para extender en clase MainActivity. Este metodo se llama automaticamente tan pronto el mapa se halla creado y cargado. El
        // argumento llamado "googleMap" y que es de tipo "GoogleMap" es el mapa que fue creado y que usaremos para asignarselo
        // a la variable global map creamos al principio.
        map = googleMap!!
        createMarker()
        createPolylines() // llamamos a este metodo para mostrar en el mapa la polylaine correspondiente.
        map.setOnMyLocationButtonClickListener(this) // usamos el metodo setOnMyLocationButtonClickListener de map,
        // y como parametro le enviamos "this" que hace referencia nuestra Activity MainActivity que esta extendiendo
        // de la clase GoogleMap.OnMyLocationButtonClickListener y que por ser una interfaz, debemos implementar
        // el metodo onMyLocationButtonClick. en resumen, estariamos llamando al metodo onMyLocationButtonClick()
        map.setOnMyLocationClickListener(this) // usamos el metodo setOnMyLocationClickListener de map,
        // y como parametro le enviamos "this" que hace referencia nuestra Activity MainActivity que esta extendiendo
        // de la clase GoogleMap.OnMyLocationClickListener y que por ser una interfaz, debemos implementar
        // el metodo onMyLocationClick. en resumen, estariamos llamando al metodo onMyLocationClick()
        enableLocation() //Cuando el mapa se cree y este funcionando y el marker se halla creado y mostado en el mapa,
        // le decimos que active la localización en tiempo real.
    }

    private fun createMarker() {
        //Esta funcion es la encargada de crear un marcador, es decir un punto especifico en el mapa. y para ello
        // necesitamos unas coordenadas (latitud y longitud)
        val coordinates: LatLng = LatLng(4.748931, -74.095778)   // creamos la variable coordinates que es un
        //objeto de la clase LatLng, la cual se usa para trabajar con las coordenadas para googleMaps y asi
        // poder marcar el punto deseado. La se le dan los parametos de longitud y latitud

        val marker = MarkerOptions().position(coordinates).title("Mi Centro Comercial Favorito, jeje") // creamos y
        // personalizamos el marker creado con loas cordenadas que le pusimos a la variable "coordinates". El metodo
        //position recibe un objeto de tipo LanLog que contiene las coordenadas,,, y title es un string que sera
        // mostrado en el mapa sobre el marcador.

        map.addMarker(marker) // a la variable global map que ya tiene el mapa asignado en la función "onMapReady"
        // ahora le asignamos el marker creado para que sea mostrado en el mapa.
        map.animateCamera( // es una animación de zoom al marker o a la posicion que le digamos
            CameraUpdateFactory.newLatLngZoom(coordinates, 13f), // newLatLngZoom recibe 2 parametros, las coordenas que
            // es a donde va a hacer el zoom, y un numero de tipo float que representa cuanto zoom queremos hacer.
            4000,  // es el tiempo en milisegundos que queremos que dure la animación
            null // Es un listener que no vamos a utilizar en este momento, simplemente añadimos null.
        )
    }

    private fun createPolylines(){

        // POLYLINES => Son lineas que podemos dibujar en el mapa por ejemplo para trazar rutas, seleccionar una zona del mapa o lo que
        // se nos ocurra y podemos personalizarlo.
        // Creamos la variable polylineOptions que es un objeto de la clase PolylineOptions y al cual le agregamos varios objetos de clase
        // LatLng y cada uno recibe como parametro una latitud y una longitud, es decir estamos agredando coordenadas. Nuestra variable
        //createPolylines queda como un array de coordenadas y cuando se relaciona un punto con otro y con otro, formamos las lineas y/o rutas.

        // Usaremosvamos la pagina www.geojson.io, donde podemos dibujar lineas en el mapa para obtener las coordenadas del dibujo
        //realizado, este sitio web nos fascilita mucho la tarea para obtener las coordenadas que necesitamos.

        val polylineOptions1 = PolylineOptions()
            .add(LatLng(4.755515904918312, -74.10151720046997))
            .add(LatLng(4.750276852863073, -74.10495042800903))
            .add(LatLng(4.751666809330694, -74.11113023757935))
            .add(LatLng(4.749528413604485, -74.11224603652954))
            .add(LatLng(4.747945996497884, -74.1062808036804))
            .add(LatLng(4.748929661612737, -74.1057014465332))
            .add(LatLng(4.746470496195698, -74.1057014465332))
            .add(LatLng(4.745850357445823, -74.09647464752197))
            .add(LatLng(4.751452970056546, -74.09359931945801))
            .add(LatLng(4.755515904918312, -74.10151720046997))
            .width(5f) // Personalizamos esta polyline para que la linea sea de 5f (es un float) de grosor. por defecto es de 10f.
            .color(ContextCompat.getColor(this, R.color.red)) // Personalizamos esta polyline para que la linea sea roja

        val polylineOptions2 = PolylineOptions()
            .add(LatLng(40.419173113350965,-3.705976009368897))
            .add(LatLng( 40.4150807746539, -3.706072568893432))
            .add(LatLng( 40.41517062907432, -3.7012016773223873))
            .add(LatLng( 40.41713105928677, -3.7037122249603267))
            .add(LatLng( 40.41926296230622,  -3.701287508010864))
            .add(LatLng( 40.419173113350965, -3.7048280239105225))
            .width(5f) // Personalizamos esta polyline para que la linea sea de 5f (es un float) de grosor. por defecto es de 10f.
            .color(ContextCompat.getColor(this, R.color.kotlin)) // Personalizamos esta polyline para que la linea sea violeta

        // usamos el metodo map.addPolyline() para agregar las variables polylineOptions1 y polylineOptions2 a nuestro mapa
        // y asi mostrar las polyline creadas (los puntos de la polylineOptions2, generan la k de kotlin en Madrid España)
        //map.addPolyline(polylineOptions1)
        //map.addPolyline(polylineOptions2)


        // Las variables polyline1 y polyline2, las creamos para que contengan toda la configuración de nuestras 2 polyline de ejemplo y luego
        // poderlas personalizar. aunque tambien podemos personalizarlas directamente cuando creamos nuestra polyline, como lo hicimos con el
        // parametro .width(5f) en la polyline que le asignamos a la variable "polylineOptions2"

        val polyline1: Polyline = map.addPolyline(polylineOptions1)
        val polyline2: Polyline = map.addPolyline(polylineOptions2)

        val pattern: List<PatternItem> = listOf(Dot(), Gap(5f), Dash(30f), Gap(5f)) // Creamos la variable pattern que es una lista
        // de parametros  de tipo "PatternItem" que nos permiten personalizar las lineas que se tranzan con las polyline con el patron
        // configurado. Dot() es un punto, Gap(10f) es un espacio al cual le pusimos una  longitud de 10f, Dash(50f) es como un guion al cual
        // le pusimos una longitud de 50f. el patrón configurado se repite a traves de toda las lineas que trazan nuestra polyline

        polyline1.pattern = pattern // A nuestra polyline1 le asignamos el patrón que creamos anteriormente para personalizar las lineas
        // que se tranzan con las polyline

        polyline2.startCap = RoundCap() // personalizamos nuestro polyline para que el primer punto sea redondo
        polyline2.endCap = CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.finish3)) // personalizamos nuestro polyline para que en el
        // ultimo punto se vea una imagen, la cual que debe estar en formato png, ya que este metodo no es compatible con svg (vectores)

        polyline2.isClickable = true  // habilitamos nuestra polyline2 (lineas trazadas), sea clickeable, por defecto viene en false.

        map.setOnPolylineClickListener {
            //cuando hacemos click en las lineas que trazan la polyline2 ( la k de kotlin en Madrid españa) cambiamos el colo de la linea
            changeColorPolyline(polyline2)
        }
    }

    fun changeColorPolyline(polyline: Polyline){
        val color: Int = (0..3).random() // la variable color, toma un numero aleatorio entre el 0 y el 3
        when(color){
            0 -> polyline.color = ContextCompat.getColor(this, R.color.red)
            1 -> polyline.color = ContextCompat.getColor(this, R.color.yellow)
            2 -> polyline.color = ContextCompat.getColor(this, R.color.green)
            3 -> polyline.color = ContextCompat.getColor(this, R.color.blue)
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        //Esta función es para validar si el usuario ha o no ha aceptado el permiso de localización
        // validamos si el permiso Manifest.permission.ACCESS_FINE_LOCATION ha sido aceptado (PackageManager.PERMISSION_GRANTED)
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun enableLocation(){
        if(!::map.isInitialized) // Si el mapa no ha sido inicializado, entoces => .... Esta validación es por
        // seguridad, ya que pueda que cuando se ingrese a la función onResumeFragments(), el mapa no halla
        // sido creado aún, entonces la app saca error (explota jeje)
            return // Si el mapa no ha sido inicializado por x motivo, entonces retorna, es decir se sale de esta
        // función ya que no se podria ejecutar el codigo siguiente.
        if(isLocationPermissionGranted()){
        // si el usuario ha aceptado los permisos de logalizacion, entonces =>
            map.isMyLocationEnabled = true // Activamos la localización en tiempo real, el cual nos habilita
            // en el mapa un icono que parece una mira de un arma en un videojuego, que al hacerle click,
            // el sistema nos lleva (por defecto) a la ubicación en la que actualmente estamos en tiempo real,
            // detectado por el gps si usamos un telefono real para pruebas, y si usamos un Emulador nos lleba
            // al punto gps que tengamos configurado en (Opciones del emulador -> Location ->
            // Podemos cambiar ese punto si queremos)..... Es importante tener presente que el comportamiento
            // cuando hacemos click en ese icono, puede cambiar cuando implementamos el metodo
            // onMyLocationButtonClick
        }
        else{
            requestLocationPermission() // Si el usuario no ha aceptado el permiso de localización
                        // enntonces ejecuta esta función para solicitar los permisos.
        }
    }

    private fun requestLocationPermission(){
        // Esta funcion mostrara mensaje a usuario para que acepte o rechace el permiso para usar
        // la camara ...... Ojoo.... si el usuario rechaza el permiso, entonces no podemos mostrarle
        // el mensaje cada rato para que lo acepte, por lo que si el usuario luego necesita que si
        // se acepte el permiso, deberá hacerlo manualmente llendo al administrador de aplicaciones
        // en la configuración del telefono y activar el permiso correspondiente. Teniendo en cuenta
        // lo anterior, debemos comprobar si el usuario ya ha rechazado el permiso con anterioridad
        // o si por el contrario es la primera ves que se le muestra el mensaje para aceptarlo o
        // rechazarlo.

        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            //Este if nos valida si el con anterioridad ya se habia solicitado el permiso de localización, y
            //  el usuario no lo aceptom es decir lo rechazo, entonces:
            Toast.makeText(this, "El permiso para usar la localización ha sido rechazado con anterioridad, " +
                    "para permitirlo, debe hacerlo desde la configuración de las aplicaciones de Android ",
                Toast.LENGTH_SHORT).show()
        }
        else{
            // Si se llega aca, es por que nunca se ha solicitado al usuario que acepte o rechace
            // el permiso que se esta solicitando, entonces procedemos a solicitarle el permiso:
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
            // Como parametos de ActivityCompat.requestPermissions, le ponemos el contexto que es this,
            // tambien va un array de permisos, que para el ejemplo seria un array de un solo element que es el
            // permiso que es el la camara, pero pueden ir todos los que se requieran, y por ultimo, le damos un numero cualquiera
            // que nosotros queramos y que representa un codigo, y ese codigo es el que asociamos a los
            // permisos que estamos solicitando, de tal manera que luego podemos usarlo para validaciones en caso de
            //  que tengamos otros permisos con otros codigos y queramos hacer logica distinta. Cada vez que pidamos
            // un permiso o varios, hay que ponerle un codigo diferente.

            //===== Si el usuario acepta o rechasa el permiso, el sitema llama automaticamente a la función onRequestPermissionsResult,
            // que fue la que sobreescribimos y en esta función activamos el permiso de localización o le mostramos un Toast
            // informativo al usuario de que el permiso ha sido rechazado  ======
        }
    }

    override fun onRequestPermissionsResult( // sobreescribimos el metodo onRequestPermissionsResult
        requestCode: Int, // viene con el metodo onRequestPermissionsResult que estamos sobreescribiendo y nos
        // sirbe para validar el codigo que pusimos en el momento de solicitar los permisos.
        permissions: Array<out String>, // viene con el metodo onRequestPermissionsResult que estamos sobreescribiendo
        grantResults: IntArray  // viene con el metodo onRequestPermissionsResult que estamos sobreescribiendo y nos
        // sirbe para validar cada unos de los permisos que pusimos en el array de permisos
        // en el momento de solicitar los permisos.
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults) // viene con el metodo onRequestPermissionsResult que estamos sobreescribiendo

        when(requestCode){

            REQUEST_CODE_LOCATION -> {
                //validamos que el argumento requestCode sea = al codigo que pusimos en el momento de
                //solicitar los permisos, si no es el mismo codigo, entonces es por que esta aceptando otros permisos
                // y si si lo es, entonces =>
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // Si el array de permisos grantResults no esta vacio y ademas su elemento en la posision
                    //[0] (que sabemos que es el permiso Manifest.permission.ACCESS_FINE_LOCATION) ha sido
                    // aceptado (PackageManager.PERMISSION_GRANTED) entonces =>
                    map.isMyLocationEnabled = true // Activamos la localización en tiempo real, el cual nos habilita
                    // en el mapa un icono que parece una mira de un arma en un videojuego, que al hacerle click,
                    // el sistema nos lleva (por defecto) a la ubicación en la que actualmente estamos en tiempo real,
                    // detectado por el gps si usamos un telefono real para pruebas, y si usamos un Emulador nos lleba
                    // al punto gps que tengamos configurado en (Opciones del emulador -> Location ->
                    // Podemos cambiar ese punto si queremos)..... Es importante tener presente que el comportamiento
                    // cuando hacemos click en ese icono, puede cambiar cuando implementamos el metodo
                    // onMyLocationButtonClick
                }
                else{
                    Toast.makeText(this, "El permiso para usar la localización ha sido rechazado, Para" +
                            "activar la localización, debe hacerlo desde la configuración de las aplicaciones de Android",
                        Toast.LENGTH_SHORT).show()
                }
            }

            else -> {
                // Nuca deberia llegar aqui ya que este caso no deveria presentarse...
            }
        }
    }

    override fun onResumeFragments() { //Esta funcíon hace parte del ciclo de vida de un fragments y se ejecuta
        // cuando el usuario regresa a nuesta app despues de haberla suspendido para ir a usar otra app.
        // Si esta función no la hubieramos sobreescribido, entonces miestras que nuestra app esta ejecutandose,
        // el usuario podría ir la configuración de las aplicaciónes de android, buscar esta app quitar el permiso
        // de localización que ya se habia aceptado, y luego volver a nuestra app que sigue en ejecución y por
        // tanto al usar la app, el sitema nos saca error por que el permiso ya no se encuentra activo. Es por lo
        // anterior que sobreescribor esta función es importante para poder validar ese caso y evitar este error,
        // es decir debemos siempre comprobar que los permisos siguen activos despues de que el usuario cambia de
        // app y luego regresa... Sobreescribimos esta función por que es el Fragment donde esta el mapa tan pronto se crea.
        super.onResumeFragments() // esta linea hace parte de la función onResumeFragments() y hace lo que tiene
        // que hacer para su funcionamiento y despues de ello hace lo que nosotros requerimos
        if(!::map.isInitialized) // Si el mapa no ha sido inicializado, entoces => .... Esta validación es por
            // seguridad, ya que pueda que cuando se ingrese a la función onResumeFragments(), el mapa no halla
            // sido creado aún, entonces la app saca error (explota jeje)
            return // Si el mapa no ha sido inicializado por x motivo, entonces retorna, es decir se sale de esta
            // función ya que no se podria ejecutar el codigo siguiente.
        if(!isLocationPermissionGranted()) // Si el permiso de localización no esta activo
            map.isMyLocationEnabled = false // desactivamos la localización en tiempo real el cual nos inactiva
             // en el mapa el icono que parece una mira de un arma en un videojuego.
             Toast.makeText(this, "El permiso para usar la localización ha sido rechazado, Para" +
                "activar la localización, debe hacerlo desde la configuración de las aplicaciones de Android",
                Toast.LENGTH_SHORT).show()
    }

    override fun onMyLocationButtonClick(): Boolean { // Este metodo es un listener (escucha) que estamos
        // obligados a sobreescribir por que nuestra activity "MainActivity" esta extendiendo de la Interfaz
        // GoogleMap.OnMyLocationButtonClickListener y retorna un boolean. Este metodo nos ayuda a personalizar
        // el comportamiento cuando se hace click en el icono del mapa que parece una mira de un arma en un
        // videojuego y que solo esta activo cuaando la localización en tiempo real esta activa (map.isMyLocationEnabled = true )
        Toast.makeText(this, "Se ha hecho click en el boton de que parece un arma de un videojuego, jaja, que se usa para" +
                "la ubicación en tiempo real", Toast.LENGTH_SHORT).show()
        return false   // Si este metodo retorna un false el sistema nos lleva (por defecto) a la ubicación
        // en la que actualmente estamos en tiempo real, detectado por el gps si usamos un telefono real para
        // pruebas, y si usamos un Emulador nos lleba al punto gps que tengamos configurado en (Opciones del emulador
        // -> Location -> Podemos cambiar ese punto si queremos)........... y si retorna un "true" no hace lo explicado
        // anteriormente y en su lugar hace lo codificado en esta función, por ejemplo antes del return podriamos
        // configurar un punto especifico con un marker como lo hicimos con la función createMarker()

    }

    override fun onMyLocationClick(p0: Location) { // Este metodo es un listener(escucha) que estamos obligados a
        // sobreescribir por que nuestra activity "MainActivity" esta extendiendo de la Interfaz
        // GoogleMap.OnMyLocationClickListener. Este metodo nos ayuda a personalizar el comportamiento cuando hacemos clic
        // en el punto donde estamos localizados en tiempo real, es decir en p0 que es de clase Location y por ello cuenta con
        //los parametros "p0.latitude" y "p0.longitude"
        Toast.makeText(this, "Tu ubicación en tiempo real es =>  ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }


}

