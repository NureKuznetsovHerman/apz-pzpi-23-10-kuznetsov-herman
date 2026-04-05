//Запити до ШІ : Створи три приклади коду на C++ для демонстрації патерну проектування Visitor
//Рівень 1: Базовий(Концептуальний) Розробити мінімальну програмну реалізацію шаблону Visitor мовою C++ для ієрархії геометричних фігур(Коло, Прямокутник).Необхідно продемонструвати механізм подвійної диспетчеризації для операції обчислення площі без зміни класів фігур
//Рівень 2 : Середній(Прикладний) Виконати програмну реалізацію шаблону Visitor для системи обліку банківських рахунків(Депозитний, Поточний).Реалізувати двох відвідувачів : перший для нарахування відсотків за місяць, другий для формування звіту про стан рахунків у консоль.Забезпечити підтримку колекції об'єктів різних типів
//Рівень 3 : Високий(Архітектурний) Спроектувати складну структуру електронного документа(Текст, Зображення, Таблиця) з використанням патерна Visitor.Необхідно реалізувати відвідувача для експорту структури документа у формат XML та відвідувача для валідації вмісту(перевірка на порожнечу даних).Використати сучасні стандарти C++ (smart pointers) та забезпечити глибоку розв'язність компонентів
//Кожен приклад повинен бути окремим фрагментом коду з коментарями і готовий до компіляції.

#include <iostream>
#include <vector>
#include <memory>

// Випереджальне оголошення
class Circle;
class Rectangle;

// Абстрактний відвідувач
class ShapeVisitor {
public:
    virtual ~ShapeVisitor() = default;
    virtual void visitCircle(Circle* c) = 0;
    virtual void visitRectangle(Rectangle* r) = 0;
};

// Базовий клас елемента
class Shape {
public:
    virtual ~Shape() = default;
    virtual void accept(ShapeVisitor* visitor) = 0;
};

// Конкретний елемент: Коло
class Circle : public Shape {
public:
    double radius;
    Circle(double r) : radius(r) {}
    void accept(ShapeVisitor* visitor) override { visitor->visitCircle(this); }
};

// Конкретний елемент: Прямокутник
class Rectangle : public Shape {
public:
    double width, height;
    Rectangle(double w, double h) : width(w), height(h) {}
    void accept(ShapeVisitor* visitor) override { visitor->visitRectangle(this); }
};

// Конкретний відвідувач: Обчислення площі
class AreaVisitor : public ShapeVisitor {
public:
    void visitCircle(Circle* c) override {
        std::cout << "Площа кола: " << 3.14159 * c->radius * c->radius << std::endl;
    }
    void visitRectangle(Rectangle* r) override {
        std::cout << "Площа прямокутника: " << r->width * r->height << std::endl;
    }
};

int main() {
    std::vector<std::unique_ptr<Shape>> shapes;
    shapes.push_back(std::make_unique<Circle>(5.0));
    shapes.push_back(std::make_unique<Rectangle>(4.0, 6.0));

    AreaVisitor areaCalc;
    for (const auto& s : shapes) {
        s->accept(&areaCalc);
    }
    return 0;
}

/////////////////////////////////////////////////////////////////////
#include <iostream>
#include <string>
#include <vector>

class SavingsAccount;
class CheckingAccount;

// Інтерфейс відвідувача для банківських операцій
class AccountVisitor {
public:
    virtual void visit(SavingsAccount& acc) = 0;
    virtual void visit(CheckingAccount& acc) = 0;
    virtual ~AccountVisitor() = default;
};

// Базовий клас банківського рахунку
class BankAccount {
protected:
    double balance;
public:
    BankAccount(double b) : balance(b) {}
    virtual void accept(AccountVisitor& visitor) = 0;
    double getBalance() const { return balance; }
    void setBalance(double b) { balance = b; }
    virtual ~BankAccount() = default;
};

class SavingsAccount : public BankAccount {
public:
    SavingsAccount(double b) : BankAccount(b) {}
    void accept(AccountVisitor& visitor) override { visitor.visit(*this); }
};

class CheckingAccount : public BankAccount {
public:
    CheckingAccount(double b) : BankAccount(b) {}
    void accept(AccountVisitor& visitor) override { visitor.visit(*this); }
};

// Конкретний відвідувач: Нарахування відсотків
class InterestVisitor : public AccountVisitor {
public:
    void visit(SavingsAccount& acc) override {
        acc.setBalance(acc.getBalance() * 1.05); // +5%
    }
    void visit(CheckingAccount& acc) override {
        acc.setBalance(acc.getBalance() * 1.01); // +1%
    }
};

// Конкретний відвідувач: Генерація звіту
class ReportVisitor : public AccountVisitor {
public:
    void visit(SavingsAccount& acc) override {
        std::cout << "Ощадний рахунок. Баланс: " << acc.getBalance() << " грн." << std::endl;
    }
    void visit(CheckingAccount& acc) override {
        std::cout << "Поточний рахунок. Баланс: " << acc.getBalance() << " грн." << std::endl;
    }
};

int main() {
    std::vector<BankAccount*> bank;
    bank.push_back(new SavingsAccount(1000));
    bank.push_back(new CheckingAccount(500));

    InterestVisitor interest;
    ReportVisitor report;

    for (auto acc : bank) acc->accept(interest);
    for (auto acc : bank) acc->accept(report);

    for (auto acc : bank) delete acc;
    return 0;
}
/////////////////////////////////////////////////////////////////
#include <iostream>
#include <vector>
#include <memory>
#include <string>

class TextElement;
class ImageElement;
class TableElement;

// Абстракція відвідувача з повною ієрархією компонентів документа
class DocumentVisitor {
public:
    virtual void visitText(const TextElement& el) = 0;
    virtual void visitImage(const ImageElement& el) = 0;
    virtual void visitTable(const TableElement& el) = 0;
    virtual ~DocumentVisitor() = default;
};

// Інтерфейс вузла документа
class DocumentNode {
public:
    virtual void accept(DocumentVisitor& v) const = 0;
    virtual ~DocumentNode() = default;
};

class TextElement : public DocumentNode {
    std::string content;
public:
    explicit TextElement(std::string c) : content(std::move(c)) {}
    void accept(DocumentVisitor& v) const override { v.visitText(*this); }
    std::string getText() const { return content; }
};

class ImageElement : public DocumentNode {
    std::string path;
public:
    explicit ImageElement(std::string p) : path(std::move(p)) {}
    void accept(DocumentVisitor& v) const override { v.visitImage(*this); }
    std::string getPath() const { return path; }
};

class TableElement : public DocumentNode {
    int rows, cols;
public:
    TableElement(int r, int c) : rows(r), cols(c) {}
    void accept(DocumentVisitor& v) const override { v.visitTable(*this); }
    std::pair<int, int> getSize() const { return { rows, cols }; }
};

// Відвідувач: Експорт у формат XML
class XmlExportVisitor : public DocumentVisitor {
public:
    void visitText(const TextElement& el) override {
        std::cout << "<text>" << el.getText() << "</text>\n";
    }
    void visitImage(const ImageElement& el) override {
        std::cout << "<image path=\"" << el.getPath() << "\"/>\n";
    }
    void visitTable(const TableElement& el) override {
        auto size = el.getSize();
        std::cout << "<table rows=\"" << size.first << "\" cols=\"" << size.second << "\"/>\n";
    }
};

// Відвідувач: Валідатор цілісності даних
class ValidationVisitor : public DocumentVisitor {
    bool all_valid = true;
public:
    void visitText(const TextElement& el) override {
        if (el.getText().empty()) all_valid = false;
    }
    void visitImage(const ImageElement& el) override {
        if (el.getPath().empty()) all_valid = false;
    }
    void visitTable(const TableElement& el) override {
        if (el.getSize().first <= 0) all_valid = false;
    }
    bool result() const { return all_valid; }
};

int main() {
    std::vector<std::unique_ptr<DocumentNode>> document;
    document.push_back(std::make_unique<TextElement>("Заголовок звіту"));
    document.push_back(std::make_unique<ImageElement>("logo.png"));
    document.push_back(std::make_unique<TableElement>(10, 3));

    XmlExportVisitor xmlExporter;
    std::cout << "--- XML Експорт ---\n";
    for (const auto& node : document) {
        node->accept(xmlExporter);
    }

    ValidationVisitor validator;
    for (const auto& node : document) {
        node->accept(validator);
    }
    std::cout << "Статус валідації: " << (validator.result() ? "Коректно" : "Помилка") << std::endl;

    return 0;
}
