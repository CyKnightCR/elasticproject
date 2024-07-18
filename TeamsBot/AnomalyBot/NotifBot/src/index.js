const notificationTemplate = require("./adaptiveCards/notification-default.json");
const { notificationApp } = require("./internal/initialize");
const { AdaptiveCards } = require("@microsoft/adaptivecards-tools");
const { TeamsBot } = require("./teamsBot");
const restify = require("restify");

// Create HTTP server.
const server = restify.createServer();
server.use(restify.plugins.bodyParser());
server.listen(process.env.port || process.env.PORT || 3978, () => {
  console.log(`\nApp Started, ${server.name} listening to ${server.url}`);
});

// HTTP trigger to send notification. You need to add authentication / authorization for this API. Refer https://aka.ms/teamsfx-notification for more details.
server.post(
  "/api/notification",
  restify.plugins.queryParser(),
  restify.plugins.bodyParser(), // Add more parsers if needed
  async (req, res) => {
    const { title, notificationUrl } = req.body; // Extract title and notificationUrl from the request body

    if (!title || !notificationUrl) {
      res.status(400).send("Invalid request. 'title' and 'notificationUrl' are required.");
      return;
    }

    const pageSize = 100;
    let continuationToken = undefined;
    do {
      const pagedData = await notificationApp.notification.getPagedInstallations(
        pageSize,
        continuationToken
      );
      const installations = pagedData.data;
      continuationToken = pagedData.continuationToken;

      for (const target of installations) {
        await target.sendAdaptiveCard(
          AdaptiveCards.declare(notificationTemplate).render({
            title: title, // Use title from request body
            appName: "AnomalyDetector Notification",
            description: `Test notification for alert to ${target.type} from AnomalyDetector notifying problem queries.`,
            notificationUrl: notificationUrl, // Use notificationUrl from request body
          })
        );

      }
    } while (continuationToken);
    
    res.json({});
  }
);

// Bot Framework message handler.
const teamsBot = new TeamsBot();
server.post("/api/messages", async (req, res) => {
  await notificationApp.requestHandler(req, res, async (context) => {
    await teamsBot.run(context);
  });
});
