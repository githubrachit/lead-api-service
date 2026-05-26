# lead-api-service
A Spring Boot REST API that acts as the entry point for the platform. It accepts lead requests from clients, validates the input, persists the lead in PostgreSQL with status "ACCEPTED", and publishes an event to AWS SNS for asynchronous processing. It also exposes endpoints to check lead status and retry failed leads.
