-------------------------------------------------------------------------------
SEAMLESS SEAMFRAME | Version: 1.2.1                      Release: March 9, 2010
-------------------------------------------------------------------------------
Copyright (c) 2009, The SEAMLESS Association. Please see the included license
files for LEGAL statements and disclaimers. It can also be obtained from the
website: http://www.seamlessassociation.org.
-------------------------------------------------------------------------------

1. Introduction

This is the source code for the SEAMLESS SEAMFRAME library. It contains the
core functionality for working with OpenMI 1.4 compliant models and model
compositions (also known as model chains). It builds upon the standard OpenMI
Java SDK, included as version 1.4.0_1 with full source code and javadoc for
convenience. On top of that a small layer has been implemented (called 'sofa')
to allow working with ontology and Java classes generated from it, as data
types to be exchanged between models.

2. Remarks for this version

This library is functional, but currently in an intermediate stage. Some major
updating/refactoring is in progress, which includes renaming of the packages
from org.seamless_ip to org.seamless_if. This matches the step from the initial
EU Integrated Project (ip) to the current SEAMLESS Association. In the future
all packages should start with org.seamless_if.

A rewrite of the job scheduler components for automated running of model
chains is also in progress. Most of the code is finished and available in the
processing package, but further testing and implementation needs to be done.

3. Available packages

- org.seamless_if.domain
	Classes providing Hibernate based access to a database generated from
	the ontology used by SEAMLESS.
	
- org.seamless_if.sofa
	The added layer to use OpenMI with classes generated from the ontology.
	
- org.seamless_if.processing.scheduler
	WORK IN PROGRESS: Scheduler for automated running of model chains.

- org.seamless_if.processing.worker
	WORK IN PROGRESS: Support classes for building Worker clients that can
	run model chains upon the Scheduler's request.
	
- org.seamless_ip.core
	Core utility classes used in the library e.g. for working with files and
	processes.

- org.seamless_ip.modelling.components
	Utility OpenMI compliant components that can be used to included standard
	operations into a model chain. For example to access the database through
	the DomainManager, to retrieve the Experiment to be processed by the model
	chain, and to store results in the database.

4. Building
This library and its source code is provided as an Eclipse (3.5) compatible
project, including all referenced libraries, project settings files and
classpath definition. It should be no problem to get started building and
using this library in the Eclipse IDE. An Ant build.xml file is also included
for building the source code. To create documentation from the source code
please run javadoc.

-------------------------------------------------------------------------------
