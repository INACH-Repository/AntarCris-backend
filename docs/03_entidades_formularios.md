# Manejo y configuracion de entidades
Los sistemas CRIS traen por defecto muchas entidades, y en varios casos no todas son necesarias 
o las versiones que vienen por defecto no tienen todo lo necesario, es frente a este escenario 
que se escribio este archivo, para indicar donde ir y que editar para poder editar los campos de las 
entidades.
Para todo lo siguiente, estaremos trabajando en la carpeta `dspace/config` y se haran referencia 
a archivos de esa carpeta.

## Esquemas de metadatos
Los sistemas CRIS utilizan una gran cantidad de esquemas de metadatos los cuales se definen en la 
carpeta `/registries`, en ella se pueden encontrar diferentes `.xml` donde cada uno representa un esquema.

En caso de querer un nuevo esquema de metados, solo se tiene que crear un nuevo archivo `.xml`, para este 
sistema CRIS, se creo uno llamado `antarc-types.xml`, la estructura que estos deben tener es simple:
```
<dspace-dc-types>

    <dspace-header>
        <title>INACH Custom Metadata</title>
    </dspace-header>

    <dc-schema>
        <name>antarc</name>
        <namespace>https://inach.cl</namespace>
    </dc-schema>

    <dc-type>
        <schema>antarc</schema>
        <element>affiliation</element>
        <qualifier>ror</qualifier>
        <scope_note></scope_note>
    </dc-type>
</dspace-dc-types>
```
Los campos `<dspace-header>` y `<dc-schema>` son obligatorios y le dan una identifacion al esquema, 
por otro lado en los `<dc-type>` son donde se definen las rutas de los metadatos.

Todos los metadatos que se definan aca pueden ser utilizados en los formularios de la siguiente seccion.
Para hacer que el sistema reconosca este nuevo esquema, se tiene que editar el archivo `dspace.cfg`, donde hay 
una seccion llamada `##### Registry Loader #####`, en ella se tiene que agregar este nuevo esquema.

## Formularios de entidades
Ahora que tenemos el nuevo esquema, lo siguiente es explicar el como se pueden ir editando los datos que tiene 
cada entidad, hay que tener cuidado en caso de querer borrar campos, ya que, estos pueden romper filtros.

Los dos archivos que nos interesan son el `item-submission.xml` y el `submission-forms.xml`. Vamos a partir con el 
segundo, en este se definen grupos de campos, podemos entenderlos como miniformularios que despues son incorporados 
en el formulario final que se define en el `item-submission.xml`, para ejemplificar tomamos el siguiente:
```
<form name="publication_indexing">
    <row>
        <field>
          <dc-schema>dc</dc-schema>
          <dc-element>subject</dc-element>
          <label>Subject Keywords</label>
          <input-type>tag</input-type>
          <repeatable>true</repeatable>
          <required />
          <hint>Type the appropriate keyword or phrase and press Enter to add it.</hint>
        </field>
    </row>
    <row>
        <field>
          <dc-schema>dc</dc-schema>
          <dc-element>description</dc-element>
          <dc-qualifier>abstract</dc-qualifier>
          <label>Abstract</label>
          <input-type>textarea</input-type>
          <repeatable>false</repeatable>
          <required />
          <hint>Enter the abstract of the item.</hint>
        </field>
    </row>
    <row>
        <field>
          <dc-schema>antarc</dc-schema>
          <dc-element>area</dc-element>
          <dc-qualifier>anid</dc-qualifier>
          <label>Linea ANID</label>
          <input-type value-pairs-name="linea_anid">dropdown</input-type>
          <hint />
        </field>
    </row>
    <row>
        <field>
          <dc-schema>antarc</dc-schema>
          <dc-element>area</dc-element>
          <dc-qualifier>inach</dc-qualifier>
          <label>Linea INACH</label>
          <input-type value-pairs-name="linea_inach">dropdown</input-type>
          <hint />
        </field>
    </row>
    <row>
        <field>
          <dc-schema>antarc</dc-schema>
          <dc-element>area</dc-element>
          <dc-qualifier>ods</dc-qualifier>
          <label>Linea ODS</label>
          <input-type value-pairs-name="linea_ods">dropdown</input-type>
          <hint />
        </field>
    </row>
</form>
```

En este ejemplo, se puede ver como el mini formulario se estructura en 5 filas donde en cada una tiene un campo. El tipo 
del dato que se pide se define en el `<input-type></input-type>`. Para que el valor se almacene correctamente, en los campos 
`<dc-schema>`, `<dc-element>`, `<dc-qualifier>` se tiene que colocar el metadato definido en el esquema correspondiente.

Un tipo de input que vale la pena destacar es el `value-pair-name`, el cual tambien se define en este archivo y corresponde a 
un tipo de input con opcion desplegable, donde tienes un conjunto de valores a elegir. Aca presentamos un ejemplo que se define 
dentro de la seccion `<form-value-pairs>` en el mismo archivo.
```
<value-pairs value-pairs-name="linea_inach" dc-term="linea_inach">
    <pair>
        <displayed-value>Estado del ecosistema antártico</displayed-value>
        <stored-value>Estado del ecosistema antártico</stored-value>
    </pair>
    <pair>
        <displayed-value>Umbrales antárticos: Resiliencia y adaptación del ecosistema</displayed-value>
        <stored-value>Umbrales antárticos: Resiliencia y adaptación del ecosistema</stored-value>
    </pair>
    <pair>
        <displayed-value>Cambio climatico en la Antártica</displayed-value>
        <stored-value>Cambio climatico en la Antártica</stored-value>
    </pair>
    <pair>
        <displayed-value>Astronomía y Ciencia de la Tierra</displayed-value>
        <stored-value>Astronimía y Ciencia de la Tierra</stored-value>
    </pair>
    <pair>
        <displayed-value>Biotecnología</displayed-value>
        <stored-value>Biotecnología</stored-value>
    </pair>
    <pair>
        <displayed-value>Huellas humanas en la Antártica</displayed-value>
        <stored-value>Huellas humanas en la Antártica</stored-value>
    </pair>
    <pair>
        <displayed-value>Ciencias Sociales y Humanidades</displayed-value>
        <stored-value>Ciencias Sociales y Humanidades</stored-value>
    </pair>
</value-pairs>
```

En este ejemplo se definio una lista para las areas de investigacion de INACH, aca hay dos campos importantes, el valor 
que se muestra al usuario `<displayed-value>` y el que es guardado en la base de datos `<stored-value>`.

En caso de querer nuevos campos, este es el archivo que se tiene que editar, y los nuevos metadatos se deben colocar 
dentro de estos mini formularios.

El siguiente archivo es `item-submission.xml`, que como se dijo anteriormente es donde se construye el formulario final en base 
a todos estos mini formularios. A continuacion se muestra un ejemplo de como luce el formulario final.
```
<submission-process name="publication-edit">
    <step id="extractionstep" />
    <step id="publication" />
    <step id="publication_indexing" />
    <step id="publication_bibliographic_details" />
    <step id="publication_references" />
</submission-process>
```
Cada uno de esos `step` corresponde a los mini formularios definidos en el `submission-forms.xml`. Cada uno de ellos se define 
como `step` usando la siguiente seccion de codigo: 
```
<step-definition id="publication_indexing" mandatory="false">
    <heading>submit.progressbar.describe.publication_indexing</heading>
    <processing-class>org.dspace.app.rest.submit.step.DescribeStep</processing-class>
    <type>submission-form</type>
</step-definition>
```
En caso de crear nuevos mini formularios estos tambien deben ser colocados aca, de lo contrario, no funcionaran.


Autor: Javier Norambuena Leiva

