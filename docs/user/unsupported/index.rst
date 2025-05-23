===========
Unsupported
===========

The modules in the unsupported directory have not passed the quality assurance steps required to be included as
part of the library.

Not everything in the GeoTools code base is ready for public abuse quite yet; for the longest time developers would keep
these experiments to themselves. With the use of our maven build system we have been able to "deploy" some of these jars
for those of you wanting a sneak peek.

The following modules are provided for your amusement only to encourage collaborate on making these modules part of our library:

Research and Development:

.. toctree::
   :maxdepth: 1

   arcgis-rest
   css
   geojson
   geoparquet
   process/index
   swing/index
   swt/index
   wps
   sample
   gml-geometry-streaming

Unsupported DataStore implementations:

* :doc:`/library/data/elasticsearch` Support for search and analytics engine
* :doc:`geoparquet` (Active) Support for reading GeoParquet format data files, both local and remote
* :doc:`/library/data/wfs-ng` (Active) Supports communication with a Web Feature Server using the standard GeoTools DataStore API
* :doc:`/library/data/mongodb` (Unknown) Support for using mongodb (https://www.mongodb.com/, https://en.wikipedia.org/wiki/MongoDB) as a feature store.

Unsupported Raster modules:

* :doc:`/library/coverage/coverageio`
* :doc:`/library/coverage/geotiff_new`
* :doc:`/library/coverage/matlab`
* :doc:`/library/coverage/multidim` - New plugins supporting NetCDF and Grib formats
* :doc:`/library/coverage/tools`

.. note::

   If you really need one of these modules to work?

   These unsupported modules are shared to illustrate ideas that has been started but not completed. They are available for any developer to pick up and work on - just ask!

   Several of the unsupported modules (for example process) are really popular. That does not however mean that there is anyone in the
   GeoTools community taking care, fixing bugs and providing documentation. In some cases these modules are the work of students, in others
   a commercial company that donated the code (but in both cases more resources are needed to make the module usable and trustworthy).

   If you really do need one of these modules cleaned up, perhaps for a deadline, please check out our support page - a
   range of commercial support options are available. Any one of these organizations can be hired to bring these modules up to speed.
