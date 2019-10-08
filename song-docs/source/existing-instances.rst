.. _existing_instances_ref:

=====================
Existing Instances
=====================

If you want to play with SONG from your browser, simply visit the Swagger UI for each server

Overture
=================
The Overture SONG server (https://song.qa.overture.bio) is configured to communicate with an Overture SCORE server (https://score.qa.overture.bio) using access tokens managed by the Overture EGO server (https://ego.qa.overture.bio). In order to perform authorized requests, an access token with **song.WRITE** or **song.<studyId>.WRITE** scopes are required.

Swagger UI: https://song.qa.overture.bio/api-docs

ICGC-DCC
=============

.. _icgc_dcc_locations_ref:

Locations
------------

.. generated at https://staticmapmaker.com/google/

.. image:: song_projects_static_map.png
.. .. image:: https://maps.googleapis.com/maps/api/staticmap?autoscale=false&size=600x300&maptype=roadmap&format=png&visual_refresh=true&markers=size:mid%7Ccolor:0xff0000%7Clabel:1%7CToronto&markers=size:mid%7Ccolor:0xffb100%7Clabel:2%7CVirginia&markers=size:mid%7Ccolor:0x0a00ff%7Clabel:3%7CBerlin&markers=size:mid%7Ccolor:0x00d70b%7Clabel:4%7CHeidelberg

.. .. image:: https://maps.googleapis.com/maps/api/staticmap?autoscale=false&size=600x300&maptype=roadmap&format=png&visual_refresh=true&markers=size:mid%7Ccolor:0xff0000%7Clabel:1%7CToronto&markers=size:mid%7Ccolor:0xffb100%7Clabel:2%7CVirginia

.. .. image:: https://maps.googleapis.com/maps/api/staticmap?autoscale=2&size=600x300&maptype=roadmap&format=png&visual_refresh=true&markers=size:mid%7Ccolor:0xff0000%7Clabel:2%7CAWS+Virginia&markers=size:mid%7Ccolor:0xff0000%7Clabel:1%7CCancer+Collaboratory+Toronto

**Legend**:

.. raw:: html

    <ul style="list-style-type:none" >
        <li>
            <strong>
                <font color="red">
                    Cancer Collaboratory - Toronto
                </font>
            </strong>
        </li>
        <li>
            <strong>
                <font color="orange">
                    AWS - Virginia
                </font>
            </strong>
        </li>
        <!-- Only add back in when both servers are live
        <li>
            <strong>
                <font color="blue">
                    DKFZ - Berlin
                </font>
            </strong> 
        </li>
        <li>
            <strong>
                <font color="green">
                    DKFZ - Heidelberg
                </font>
            </strong>
        </li>
        -->
    </ul>


.. _cancer_collab_toronto_ref:

Cancer Collaboratory - Toronto
-----------------------------------
Swagger UI: https://song.cancercollaboratory.org/api-docs.

In order to interact with this SONG server, the authorization scopes **collab.WRITE** or **collab.<studyId>.WRITE** are required. 
This server is configured to operate with the **Cancer Collaboratory - Toronto** SCORE server (https://storage.cancercollaboratory.org), which requires **collab.WRITE** scope. 
For more information about user access, refer to the :ref:`ICGC-DCC User Access <icgc_dcc_user_access_ref>` documentation.


.. _aws_virginia_ref:

AWS - Virginia
--------------------------
Swagger UI: https://virginia.song.icgc.org/api-docs.

In order to interact with this SONG server, the authorization scopes **aws.WRITE** or **aws.<studyId>.WRITE** are required.
This server is configured to operate with the **AWS - Virginia** SCORE server (https://virginia.storage.icgc.org), which requires **aws.WRITE** scope. 
For more information about user access, refer to the :ref:`ICGC-DCC User Access <icgc_dcc_user_access_ref>` documentation.

.. _icgc_user_access_ref:

User Access
--------------------------

DACO Authentication
......................

The :ref:`cancer_collab_toronto_ref` and :ref:`aws_virginia_ref` SONG servers use the `auth.icgc.org <https://auth.icgc.org>`_ OAuth2 authorization service to authorize secure API requests.
In order to create the neccessary access tokens to interact with the song-python-sdk and the SONG server,
the user **must** have DACO access. For more information about obtaining DACO access, please visit the instructions for
`DACO Cloud Access <http://docs.icgc.org/download/guide/#daco-cloud-access>`_.


OAuth2 Authorization
.........................

With proper DACO access, the user can create an access token, using
the `Access Tokens <http://docs.icgc.org/download/guide/#access-tokens>`_
and `Token Manager <http://docs.icgc.org/download/guide/#token-manager>`_ instructions with the correct scopes.
