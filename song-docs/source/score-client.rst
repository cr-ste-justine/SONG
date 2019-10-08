.. _score_client_ref:

======================
SCORe Client
======================

The SCORe client (formally the :term:`icgc-storage-client`) is used to upload and download object data to and from the SCORe Server. 

.. todo::
    replace the seealso with the official score readthe docs

.. seealso::
    For more information about SCORE, refer to `<https://www.overture.bio/products/score>`_

Installation
=================

For installation, please see `Installing SCORe client from Tarball <http://docs.icgc.org/download/guide/#install-from-tarball>`_ instructions.

Configuration
===============
For configuration, after un-archiving the tarball, modify the ``./conf/application.properties`` by adding the line:

.. code-block:: bash

    accessToken=<my_access_token>

where the accessToken has the appropriate scope.


Usage
==============

.. todo::
    replace this link when score read the docs is up

For more information about the usage of the client, refer to `SCORe Client Usage <https://docs.icgc.org/download/guide/#score-client-usage>`_ documentation.
