import XCTest

/// Taps "Random progress" and keeps screenshots of the animation (see the .xcresult attachments).
final class SmokeUITests: XCTestCase {
    func testProgressAnimates() {
        let app = XCUIApplication()
        app.launch()
        sleep(2)
        attachScreenshot("start")

        // Compose draws its own UI, so tap by position: the button is at the top left
        app.coordinate(withNormalizedOffset: CGVector(dx: 0.25, dy: 0.155)).tap()
        for step in 1...6 {
            usleep(200_000)
            attachScreenshot("after tap \(step)")
        }
        sleep(2)
        attachScreenshot("end")
    }

    private func attachScreenshot(_ name: String) {
        let attachment = XCTAttachment(screenshot: XCUIScreen.main.screenshot())
        attachment.name = name
        attachment.lifetime = .keepAlways
        add(attachment)
    }
}
