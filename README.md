# 4156-Miniproject-2025-Students

This is the public repo for posting the miniproject assignments to the class.

This is a template repository. See https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-repository-from-a-template.

## Static Bug Analysis with PMD

This project uses PMD (Programming Mistake Detector) for static code analysis to identify potential bugs, code smells, and maintain code quality.

### PMD Installation

#### Option 1: Using Homebrew (macOS)
```bash
brew install pmd
```

#### Option 2: Using Maven Plugin (Cross-platform)
PMD can also be run via Maven plugin by adding the following to your `pom.xml`:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-pmd-plugin</artifactId>
    <version>3.21.0</version>
    <configuration>
        <rulesets>
            <ruleset>rulesets/java/quickstart.xml</ruleset>
        </rulesets>
    </configuration>
</plugin>
```

### Running PMD Analysis

#### Command Line Usage
```bash
# Run PMD analysis on source code
pmd check -d src/main/java -R rulesets/java/quickstart.xml -f text

# Generate HTML report
pmd check -d src/main/java -R rulesets/java/quickstart.xml -f html -r pmd-report.html

# Run with specific rule categories
pmd check -d src/main/java -R category/java/errorprone.xml,category/java/bestpractices.xml -f text
```

#### Maven Integration
```bash
# Generate PMD report via Maven
mvn pmd:pmd

# Run PMD check (fails build on violations)  
mvn pmd:check

# View report
open target/site/pmd.html
```

### Understanding PMD Output

PMD reports issues with the following format:
```
src/main/java/package/ClassName.java:line_number: RuleName: Description of the issue
```

Common rule categories:
- **Error Prone**: Likely bugs or error conditions
- **Best Practices**: Coding best practices violations
- **Code Style**: Formatting and naming conventions  
- **Design**: Object-oriented design principles
- **Performance**: Performance-related issues

### PMD Configuration

Create a custom PMD ruleset by creating `pmd-ruleset.xml`:
```xml
<?xml version="1.0"?>
<ruleset name="Custom Rules">
    <description>Custom PMD rules for this project</description>
    
    <rule ref="category/java/errorprone.xml"/>
    <rule ref="category/java/bestpractices.xml"/>
    <rule ref="category/java/codestyle.xml">
        <exclude name="AtLeastOneConstructor"/>
    </rule>
</ruleset>
```

### Bug Fixing Results

This project underwent comprehensive bug fixing using PMD analysis combined with unit testing:
- **Total Bugs Fixed**: 12 critical bugs
- **Test Success Rate**: Improved from 50% to 100%  
- **PMD Violations**: Significantly reduced static analysis warnings
- **Code Coverage**: Maintained while improving code quality

See `bugs.md` for detailed documentation of all fixed bugs.

## Testing

Run the complete test suite:
```bash
./mvnw test
```

Run with checkstyle validation:
```bash  
./mvnw checkstyle:check test
```