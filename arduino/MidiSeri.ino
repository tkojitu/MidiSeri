class ToneGenerator {
private:
  int pin;

public:
  ToneGenerator(int pin) : pin(pin) {}
  ~ToneGenerator() {}

  void tone(int note) {
    if (note <= 0) {
      ::noTone(pin);
    } else {
      ::tone(pin, note);
    }
  }
};

class App {
private:
  ToneGenerator* gen;
  char buffer[128];

public:
  App() : gen(NULL) {
    gen = new ToneGenerator(8);
    clearBuffer();
  }

  ~App() {
    delete gen;
  }

  void clearBuffer() {
    for (size_t i = 0; i < sizeof(buffer); ++i) {
      buffer[i] = 0;
    }
  }

  void setup() {
    Serial.begin(9600);
    Serial.setTimeout(1000);
  }

  void loop() {
    if (Serial.available() <= 0) {
     return;
    }
    if (Serial.peek() == '\n') {
      Serial.read();
      return;
    }
    int note = Serial.parseInt();
    gen->tone(note);
    Serial.println(note);
  }
};

void* gApp;

void setup() {
  App* app = new App();
  app->setup();
  gApp = app;
}

void loop() {
  ((App*)gApp)->loop();
}

