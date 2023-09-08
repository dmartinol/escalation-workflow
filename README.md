# Simple escalation workflow
An escalation workflow integrated with Atlassian JIRA using [SonataFlow](https://sonataflow.org/serverlessworkflow/latest/index.html).

## Escalation flow

    Create a new JIRA ticket ->
    
    Watch Jira ticket status (Wait for 'Done' status) ->
    
        if approved before timeout ->
            Create the namespace in configured Red Hat Openshift Cluster
    
        if timeout has reached ->
            Send email to provided email address

## Hardcoded settings
Polling periodicity and escalation timeout are hardcoded in [ticketEscalation.sw.yaml](./src/main/resources/ticketEscalation.sw.yaml):
```yaml
states:
...
  - name: GetJiraIssue
...
        sleep:
          before: PT6S
...
  - name: TicketDone
    type: switch
    dataConditions:
      - condition: (.jiraIssue.fields.status.name == "Done" and .jiraIssue.fields.status.statusCategory.name == "Done")
        transition:
          nextState: CreateK8sNamespace
      - condition: (.jiraIssue.fields.status.name != "Done" and .timer.triggered == false and .timer.elapsedSeconds > 10)
        transition:
          nextState: Escalate
...
```

## How to run
```bash
export SMTP_HOST=YOUR_SMTP_HOST
export SMTP_PORT=YOUR_SMTP_PORT
export SMTP_USERNAME="YOUR_EMAIL"
export SMTP_PASSWORD="YOUR_SMTP_PASSSWORD"

mvn clean quarkus:dev
```

Example for Gmail SMTP (see [Sign in with app passwords](https://support.google.com/mail/answer/185833?hl=en)):
```bash
export SMTP_HOST=smtp.gmail.com
export SMTP_PORT=587
export SMTP_USERNAME="YOUR_EMAIL@gmail.com"
export SMTP_PASSWORD="YOUR_API_PASSWORD"

mvn clean quarkus:dev
```

Example of POST to trigger the flow:
```bash
curl -XPOST -H "Content-Type: application/json" http://localhost:8080/ticket-escalation -d '{"namespace": "my-new-namespace", "manager": "dmartino@redhat.com"}'
```

Tips:
* Visit [Workflow Instances](http://localhost:8080/q/dev/org.kie.kogito.kogito-quarkus-serverless-workflow-devui/workflowInstances)
* Visit (Data Index Query Service)[http://localhost:8080/q/graphql-ui/]