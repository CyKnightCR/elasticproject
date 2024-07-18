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
            title: "New Anomaly Occurred!",
            appName: "AnomalyDetector Notification",
            description: `This is a sample http-triggered notification to ${target.type} from AnomalyDetector notifying problem queries.`,
            notificationUrl: "https://aka.ms/teamsfx-notification-new",
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
